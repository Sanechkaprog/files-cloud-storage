package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.exceptions.ResourceNotExistException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import com.alexkrug.files_cloud_storage.storage.service.ObjectStorageService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {
    private final DirectoryService directoryService;
    private final Log log = LogFactory.getLog(FileService.class);
    private final ObjectStorageService objectStorageService;
    private final ResourceRepository resourceRepository;

    public FileService(DirectoryService directoryService, ObjectStorageService objectStorageService, ResourceRepository resourceRepository) {
        this.directoryService = directoryService;
        this.objectStorageService = objectStorageService;
        this.resourceRepository = resourceRepository;
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
            if (ResourceType.FILE.equals(resourceRecord.getType())) {
                dtos.add(new FileDto(
                        pathWithoutPrefix,
                        resourceRecord.getName(),
                        resourceRecord.getSize(),
                        resourceRecord.getType()
                ));
            } else {
                dtos.add(new DirectoryDto(
                        pathWithoutPrefix,
                        resourceRecord.getName(),
                        resourceRecord.getType()
                ));
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
                objectStorageService.listObjects(prefix);

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
}