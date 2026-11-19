package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.FileDto;
import com.alexkrug.files_cloud_storage.resources.api.dto.ResourceDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import com.alexkrug.files_cloud_storage.resources.tools.PathResolver;
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
            objectStorage.createDirectory(path);
            String modifiedPath = path.substring(0, path.lastIndexOf('/'));
            int lastIndex = modifiedPath.lastIndexOf('/');
            int firstIndex = modifiedPath.indexOf('/');


            String parentPath = modifiedPath.substring(0, lastIndex + 1);
            String folder = modifiedPath.substring(lastIndex + 1);

            ResourceRecord resourceRecord = new ResourceRecord(
                    parentPath,
                    folder,
                    null,
                    ResourceType.DIRECTORY
            );

            resourceRepository.save(resourceRecord);

            String dtoPath = modifiedPath.substring(firstIndex + 1, lastIndex + 1);
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

            String pathWithoutPrefix = PathResolver.getPathWithoutPrefix(resourcePath);


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
        String parentPath;

        int prefixLength = PathResolver.getPrefixLength(path);

        String rawPath = PathResolver.getParentPath(path);

        if (rawPath.length() == prefixLength) {
            parentPath = path;
        } else {
            String pathWithoutSuffix = PathResolver.getPathWithoutSuffix(path);

            parentPath = PathResolver.getParentPath(pathWithoutSuffix);
        }
        try {
            objectStorage.getStatObject(parentPath);
        } catch (Exception ex) {
            throw new FolderNotExistsException("Parent-folder doesn`t exist");
        }
    }

    public boolean isExistFolder(String path) {
        String pathWithoutSuffix = PathResolver.getPathWithoutSuffix(path);

        String parentPath = PathResolver.getParentPath(pathWithoutSuffix);

        String name = PathResolver.getFileName(pathWithoutSuffix);

        Optional<ResourceRecord> optionalResourceRecord = resourceRepository.findResourceRecordByPathAndName(parentPath, name);

        return optionalResourceRecord.isPresent();
    }

    public List<ResourceDto> createDirectories(String filePath) {
        List<String> paths = new ArrayList<>();


        List<ResourceDto> directories = new ArrayList<>();

        String parentPath = PathResolver.getParentPath(filePath);

        int right = parentPath.indexOf('/') + 1;

        while (right < parentPath.length()) {
            if (parentPath.charAt(right) == '/') {
                paths.add(parentPath.substring(0, right));
            }
            right++;
        }

        for (String path : paths) {
            try {
                addDirectory(path + "/");

                String pathWithoutPrefix = PathResolver.getPathWithoutPrefix(path);

                String fileDtoPath = PathResolver.getParentPath(pathWithoutPrefix);

                String name = PathResolver.getFileName(pathWithoutPrefix);

                if (fileDtoPath.equals(name)) {
                    fileDtoPath = "/";
                }

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
