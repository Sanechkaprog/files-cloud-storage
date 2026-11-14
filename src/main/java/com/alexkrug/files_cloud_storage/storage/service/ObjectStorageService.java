package com.alexkrug.files_cloud_storage.storage.service;

import com.alexkrug.files_cloud_storage.storage.exception.MinioClientException;
import com.alexkrug.files_cloud_storage.storage.exception.StatsObjectUnavailbaleGetException;
import com.alexkrug.files_cloud_storage.storage.repository.ObjectStorage;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class ObjectStorageService implements ObjectStorage {
    private final String BUCKET_NAME = "cloud-storage";
    private final MinioClient minioClient;

    public ObjectStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void createDirectory(String path) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(path)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioClientException("Error with MinioClient");
        }
    }

    @Override
    public StatObjectResponse getStatObject(String path) {
        StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build();
        try {
            return minioClient.statObject(statObjectArgs);
        } catch (Exception ex) {
            throw new StatsObjectUnavailbaleGetException("Unavailable getting stats");
        }
    }
}
