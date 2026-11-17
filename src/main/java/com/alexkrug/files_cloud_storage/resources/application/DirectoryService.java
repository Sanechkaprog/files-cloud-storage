package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import com.alexkrug.files_cloud_storage.resources.tools.ResourceMapper;
import com.alexkrug.files_cloud_storage.storage.repository.ObjectStorage;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class DirectoryService {
    private final ObjectStorage objectStorage;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public DirectoryService(ObjectStorage objectStorage, ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.objectStorage = objectStorage;
        this.resourceMapper = resourceMapper;
    }

    public DirectoryDto addDirectory(String path) {
        isExistPath(path);
        if (isExistFolder(path)) {
            throw new FolderIsAlreadyExistException("Folder is already exist");
        }
        try {
            objectStorage.createDirectory(path + "/");
            int lastIndex = path.lastIndexOf('/');
            int firstIndex = path.indexOf('/');


            String parentPath = path.substring(0, lastIndex + 1);
            String folder = path.substring(lastIndex + 1);

            ResourceRecord resourceRecord = new ResourceRecord(
                    parentPath,
                    folder,
                    null,
                    ResourceType.DIRECTORY
            );

            resourceRepository.save(resourceRecord);

            String dtoPath = path.substring(firstIndex + 1, lastIndex + 1);
            if (dtoPath.isEmpty()) {
                dtoPath = "/";
            }


            return new DirectoryDto(
                    dtoPath,
                    folder,
                    ResourceType.DIRECTORY
            );

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<ResourceDto> getDirectoryResources(String path) {
        List<ResourceDto> resourceDtos = new ArrayList<>();

        isExistPath(path);

        List<ResourceRecord> resourceRecords = resourceRepository.findResourceRecordByPath(path);

        for (ResourceRecord resourceRecord : resourceRecords) {
            String resourcePath = resourceRecord.getPath();

            String pathWithoutPrefix = resourcePath.substring(resourcePath.indexOf('/') + 1);

            if (pathWithoutPrefix.isEmpty()) {
                pathWithoutPrefix = "/";
            }

            if (ResourceType.DIRECTORY == resourceRecord.getType()) {
                DirectoryDto directoryDto = resourceMapper.toDirectoryDto(resourceRecord, pathWithoutPrefix);
                resourceDtos.add(directoryDto);
            }

            if (ResourceType.FILE == resourceRecord.getType()) {
                FileDto fileDto = resourceMapper.toFileDto(resourceRecord, pathWithoutPrefix);
                resourceDtos.add(fileDto);
            }
        }
        return resourceDtos;
    }

    public void isExistPath(String path) {
        int lastIndex = path.lastIndexOf('/');

        String parentPath = path.substring(0, lastIndex + 1);

        if (parentPath.isEmpty()) {
            parentPath = "/";
        }
        try {
            objectStorage.getStatObject(parentPath);
        } catch (Exception ex) {
            throw new FolderNotExistsException("Parent-folder doesn`t exist");
        }
    }

    public boolean isExistFolder(String path) {
        int lastIndex = path.lastIndexOf('/');

        String parentPath = path.substring(0, lastIndex + 1);

        if (parentPath.isEmpty()) {
            return true;
        }

        String name = path.substring(lastIndex + 1);

        Optional<ResourceRecord> optionalResourceRecord = resourceRepository.findResourceRecordByPathAndName(parentPath, name);

        return optionalResourceRecord.isPresent();
    }

    public List<ResourceDto> createDirectories(String filePath) {
        List<String> paths = new ArrayList<>();

        List<ResourceDto> directories = new ArrayList<>();

        String parentPath = filePath.substring(0, filePath.lastIndexOf('/'));

        int right = parentPath.indexOf('/') + 1;

        while (right < parentPath.length()) {
            if (parentPath.charAt(right) == '/') {
                paths.add(parentPath.substring(0, right));
            }
            right++;
        }
        paths.add(parentPath);
        for (String path : paths) {
            try {
                addDirectory(path);
                String pathWithoutPrefix = path.substring(path.indexOf('/') + 1);

                String fileDtoPath = pathWithoutPrefix.substring(0, pathWithoutPrefix.lastIndexOf('/') + 1);

                String name = pathWithoutPrefix.substring(pathWithoutPrefix.lastIndexOf('/') + 1);

                DirectoryDto directoryDto = new DirectoryDto(
                        fileDtoPath,
                        name,
                        ResourceType.DIRECTORY
                );

                directories.add(directoryDto);
            } catch (FolderIsAlreadyExistException ignored) {
            }
        }
        return directories;
    }
}
