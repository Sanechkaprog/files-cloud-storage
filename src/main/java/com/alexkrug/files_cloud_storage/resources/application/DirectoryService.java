package com.alexkrug.files_cloud_storage.resources.application;

import com.alexkrug.files_cloud_storage.resources.api.dto.DirectoryDto;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceRecord;
import com.alexkrug.files_cloud_storage.resources.domain.ResourceType;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.resources.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.resources.infrastucture.ResourceRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;


@Service
public class DirectoryService {
    private final MinioClient minioClient;
    private final ResourceRepository resourceRepository;
    private final Logger log = LoggerFactory.getLogger(DirectoryService.class);

    public DirectoryService(MinioClient minioClient, ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
        this.minioClient = minioClient;
    }

    public DirectoryDto addDirectory(String path) {
        isExistPath(path);
        if (isExistFolder(path)) {
            throw new FolderIsAlreadyExistException("Folder is already exist");
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("cloud-storage")
                            .object(path + "/")
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
            int lastIndex = path.lastIndexOf('/');
            int firstIndex = path.indexOf('/');


            String parentPath = path.substring(firstIndex + 1, lastIndex + 1);
            String folder = path.substring(lastIndex + 1);


            ResourceRecord resourceRecord = new ResourceRecord(
                    parentPath,
                    folder,
                    null,
                    ResourceType.DIRECTORY
            );

            resourceRepository.save(resourceRecord);

            return new DirectoryDto(
                    parentPath,
                    folder,
                    ResourceType.DIRECTORY
            );

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void isExistPath(String path) {
        int lastIndex = path.lastIndexOf('/');
        String parentPath = path.substring(0, lastIndex + 1);
        StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket("cloud-storage")
                .object(parentPath)
                .build();
        try {
            minioClient.statObject(statObjectArgs);
        } catch (Exception ex) {
            throw new FolderNotExistsException("Parent-folder doesn`t exist");
        }
    }

    private boolean isExistFolder(String path) {
        int lastIndex = path.lastIndexOf('/');
        String parentPath = path.substring(0, lastIndex + 1);
        String name = path.substring(lastIndex + 1);
        Optional<ResourceRecord> optionalResourceRecord = resourceRepository.findResourceRecordByPathAfterAndName(parentPath, name);
        return optionalResourceRecord.isPresent();
    }
}
