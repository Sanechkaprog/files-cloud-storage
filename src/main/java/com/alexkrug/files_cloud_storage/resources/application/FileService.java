package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.exceptions.ResourceIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.ResourceNotExistException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import com.alexkrug.files_cloud_storage.resources.tools.ResourceMapper;
import com.alexkrug.files_cloud_storage.storage.service.ObjectStorageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final DirectoryService directoryService;
    private final ObjectStorageService objectStorageService;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public FileService(DirectoryService directoryService, ObjectStorageService objectStorageService, ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
        this.directoryService = directoryService;
        this.objectStorageService = objectStorageService;
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    public List<ResourceDto> upload(String path, List<MultipartFile> multipartFiles) {
        if (!directoryService.isExistFolder(path)) {
            throw new FolderNotExistsException("Folder not exist");
        }
        String modifiedPath = path + "/";
        List<ResourceDto> dtos = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = modifiedPath + multipartFile.getOriginalFilename();

            dtos.addAll(directoryService.createDirectories(filePath));

            Long size = multipartFile.getSize();

            if (directoryService.isExistFolder(filePath)) {
                throw new FolderIsAlreadyExistException("File is already exist");
            }

            try (InputStream is = multipartFile.getInputStream()) {
                objectStorageService.createFile(filePath, is, size);
                FileDto fileDto = new FileDto(
                        filePath.substring(filePath.indexOf('/') + 1, filePath.lastIndexOf('/') + 1),
                        filePath.substring(filePath.lastIndexOf('/') + 1),
                        size,
                        ResourceType.FILE
                );

                dtos.add(fileDto);
                ResourceRecord resourceRecord = new ResourceRecord(
                        filePath.substring(0, filePath.lastIndexOf('/') + 1),
                        filePath.substring(filePath.lastIndexOf('/') + 1),
                        size,
                        ResourceType.FILE
                );

                resourceRepository.save(resourceRecord);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dtos;
    }

    public List<ResourceDto> search(String query) {
        List<ResourceDto> dtos = new ArrayList<>();

        List<ResourceRecord> resourceRecords = resourceRepository.findResourceRecordByNameContaining(query);

        for (ResourceRecord resourceRecord : resourceRecords) {
            String path = resourceRecord.getPath();

            String pathWithoutPrefix = path.substring(path.indexOf('/') + 1);
            if (pathWithoutPrefix.isEmpty()) {
                pathWithoutPrefix = "/";
            }

            if (ResourceType.FILE == resourceRecord.getType()) {
                dtos.add(resourceMapper.toFileDto(resourceRecord, pathWithoutPrefix));

            } else {
                dtos.add(resourceMapper.toDirectoryDto(resourceRecord, pathWithoutPrefix));
            }
        }
        return dtos;
    }

    public boolean isDirectory(String path) {
        int lastIndex = path.lastIndexOf('/');

        String parentPath = path.substring(0, lastIndex + 1);
        String name = path.substring(lastIndex + 1);

        ResourceRecord record = resourceRepository
                .findResourceRecordByPathAndName(parentPath, name)
                .orElseThrow(() ->
                        new FolderNotExistsException("Resource not found")
                );

        return record.getType() == ResourceType.DIRECTORY;
    }

    public StreamingResponseBody download(String path) {

        int lastIndex = path.lastIndexOf('/');

        String parentPath = path.substring(0, lastIndex + 1);
        String name = path.substring(lastIndex + 1);

        ResourceRecord record = resourceRepository
                .findResourceRecordByPathAndName(parentPath, name)
                .orElseThrow(() ->
                        new FolderNotExistsException("Resource not found")
                );

        if (record.getType() == ResourceType.FILE) {

            return outputStream -> {
                try (InputStream inputStream =
                             objectStorageService.getObject(path)) {

                    inputStream.transferTo(outputStream);
                    outputStream.flush();

                } catch (IOException e) {
                    throw new RuntimeException(
                            "Error downloading file", e
                    );
                }
            };
        }

        String prefix = path + "/";

        List<String> objects =
                objectStorageService.listObjects(prefix, true);

        return outputStream -> {

            try (ZipOutputStream zipOutputStream =
                         new ZipOutputStream(outputStream)) {
                for (String object : objects) {
                    String relativePath = object.substring(prefix.length());
                    if (relativePath.isEmpty()) {
                        continue;
                    }
                    ZipEntry zipEntry = new ZipEntry(relativePath);
                    zipOutputStream.putNextEntry(zipEntry);
                    try (InputStream inputStream =
                                 objectStorageService.getObject(object)) {

                        inputStream.transferTo(zipOutputStream);
                    }
                    zipOutputStream.closeEntry();
                }
                zipOutputStream.finish();

            } catch (IOException e) {
                throw new RuntimeException(
                        "Error creating ZIP archive", e
                );
            }
        };
    }

    public void deleteResource(String path) {
        String parentPath = path.substring(0, path.lastIndexOf('/') + 1);

        String name = path.substring(path.lastIndexOf('/') + 1);


        ResourceRecord resourceRecord = resourceRepository
                .findResourceRecordByPathAndName(parentPath, name)
                .orElseThrow(() -> new ResourceNotExistException("Resource is not exist"));

        if (ResourceType.FILE == resourceRecord.getType()) {
            objectStorageService.deleteFile(path);
            resourceRepository.deleteResourceRecordByPathAndName(parentPath, name);
        } else {
            List<String> deletedObjects = objectStorageService.deleteFolder(path);
            deleteResourcesFromDataBase(deletedObjects);
        }
    }

    public ResourceDto getResource(String path) {
        String pathWithoutSuffix = path.endsWith("/") ? path.substring(0, path.lastIndexOf('/')) : path;

        String parentPath = pathWithoutSuffix.substring(0, pathWithoutSuffix.lastIndexOf('/') + 1);

        String name = pathWithoutSuffix.substring(pathWithoutSuffix.lastIndexOf('/') + 1);

        ResourceRecord resourceRecord = resourceRepository.findResourceRecordByPathAndName(parentPath, name)
                .orElseThrow(() -> new ResourceNotExistException("Resource is not exist"));

        String resourcePath = resourceRecord.getPath();

        String pathWithoutPrefix = resourcePath.substring(resourcePath.indexOf('/') + 1);

        if (pathWithoutPrefix.isEmpty()) {
            pathWithoutPrefix = "/";
        }

        return resourceRecord.getType() == ResourceType.FILE ?
                resourceMapper.toFileDto(resourceRecord, pathWithoutPrefix) :
                resourceMapper.toDirectoryDto(resourceRecord, pathWithoutPrefix);

    }

    public ResourceDto moveResource(String from, String to) {

        String safeFrom = from.endsWith("/")
                ? from.substring(0, from.length() - 1)
                : from;

        String safeTo = to.endsWith("/")
                ? to.substring(0, to.length() - 1)
                : to;

        int fromIndex = safeFrom.lastIndexOf('/');
        int toIndex = safeTo.lastIndexOf('/');

        String fromParentPath = safeFrom.substring(0, fromIndex + 1);
        String fromName = safeFrom.substring(fromIndex + 1);

        String toParentPath = safeTo.substring(0, toIndex + 1);
        String toName = safeTo.substring(toIndex + 1);

        if (fromName.isBlank() || toName.isBlank()) {
            throw new IllegalStateException("Invalid resource name");
        }

        if (safeFrom.equals(safeTo)) {
            throw new IllegalStateException("Source and destination are identical");
        }

        ResourceRecord resourceRecord = resourceRepository
                .findResourceRecordByPathAndName(fromParentPath, fromName)
                .orElseThrow(() ->
                        new ResourceNotExistException("Resource does not exist")
                );

        if (resourceRepository
                .findResourceRecordByPathAndName(toParentPath, toName)
                .isPresent()) {

            throw new ResourceIsAlreadyExistException(
                    "Resource already exists"
            );
        }

        if (resourceRecord.getType() == ResourceType.DIRECTORY) {

            String oldPrefix = safeFrom + "/";

            if (safeTo.startsWith(oldPrefix)) {
                throw new IllegalStateException(
                        "Cannot move directory into itself"
                );
            }
        }

        if (resourceRecord.getType() == ResourceType.FILE) {

            objectStorageService.moveFile(safeFrom, safeTo);

            resourceRecord.setPath(toParentPath);
            resourceRecord.setName(toName);

            resourceRepository.saveAndFlush(resourceRecord);

            objectStorageService.deleteFile(safeFrom);

            String pathWithoutPrefix =
                    toParentPath.substring(toParentPath.indexOf('/') + 1);

            if (pathWithoutPrefix.isEmpty()) {
                pathWithoutPrefix = "/";
            }

            return resourceMapper.toFileDto(
                    resourceRecord,
                    pathWithoutPrefix
            );
        }

        String oldPrefix = safeFrom + "/";
        String newPrefix = safeTo + "/";

        List<String> objects =
                objectStorageService.listObjects(oldPrefix, false);

        for (String object : objects) {

            String relativePath =
                    object.substring(oldPrefix.length());

            String newObjectPath = newPrefix + relativePath;

            objectStorageService.moveFile(object, newObjectPath);
        }

        List<ResourceRecord> descendants =
                resourceRepository.findResourceRecordByPathStartingWith(
                        oldPrefix
                );

        for (ResourceRecord descendant : descendants) {

            String relativePath =
                    descendant.getPath().substring(oldPrefix.length());

            descendant.setPath(newPrefix + relativePath);
        }

        resourceRecord.setPath(toParentPath);
        resourceRecord.setName(toName);

        resourceRepository.saveAll(descendants);
        resourceRepository.saveAndFlush(resourceRecord);

        objectStorageService.deleteFolder(oldPrefix);

        String pathWithoutPrefix =
                toParentPath.substring(toParentPath.indexOf('/') + 1);

        if (pathWithoutPrefix.isEmpty()) {
            pathWithoutPrefix = "/";
        }

        return resourceMapper.toDirectoryDto(
                resourceRecord,
                pathWithoutPrefix
        );
    }


    private void deleteResourcesFromDataBase(List<String> resources) {
        for (String objectPath : resources) {
            String objectPathModified = objectPath.endsWith("/") ? objectPath.substring(0, objectPath.lastIndexOf('/')) : objectPath;

            String objectParentPath = objectPathModified.substring(0, objectPathModified.lastIndexOf('/') + 1);

            String objectName = objectPathModified.substring(objectPathModified.lastIndexOf('/') + 1);

            resourceRepository.deleteResourceRecordByPathAndName(objectParentPath, objectName);
        }
    }
}