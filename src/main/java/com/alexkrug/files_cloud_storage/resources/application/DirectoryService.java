package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import com.alexkrug.files_cloud_storage.storage.repository.ObjectStorage;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class DirectoryService {
    private final ObjectStorage objectStorage;
    private final ResourceRepository resourceRepository;

    public DirectoryService(ObjectStorage objectStorage, ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
        this.objectStorage = objectStorage;
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
            if (ResourceType.DIRECTORY.equals(resourceRecord.getType())) {
                DirectoryDto directoryDto = new DirectoryDto(
                        pathWithoutPrefix,
                        resourceRecord.getName(),
                        resourceRecord.getType()
                );
                resourceDtos.add(directoryDto);
            }

            if (ResourceType.FILE.equals(resourceRecord.getType())) {
                FileDto fileDto = new FileDto(
                        pathWithoutPrefix,
                        resourceRecord.getName(),
                        resourceRecord.getSize(),
                        resourceRecord.getType()
                );
                resourceDtos.add(fileDto);
            }
        }
        return resourceDtos;
    }


    private void isExistPath(String path) {
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

    private boolean isExistFolder(String path) {
        int lastIndex = path.lastIndexOf('/');
        String parentPath = path.substring(0, lastIndex + 1);
        if (parentPath.isEmpty()) {
            parentPath = "/";
        }

        String name = path.substring(lastIndex + 1);
        Optional<ResourceRecord> optionalResourceRecord = resourceRepository.findResourceRecordByPathAndName(parentPath, name);
        return optionalResourceRecord.isPresent();
    }
}
