package com.alexkrug.files_cloud_storage.storage.service;

import com.alexkrug.files_cloud_storage.storage.exception.MinioClientException;
import com.alexkrug.files_cloud_storage.storage.exception.StatsObjectUnavailbaleGetException;
import com.alexkrug.files_cloud_storage.storage.repository.ObjectStorage;
import io.minio.*;
import io.minio.messages.Item;
import org.flywaydb.core.extensibility.InfoTelemetryModel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void createFile(String path, InputStream is, Long size) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(path)
                            .stream(is, size, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioClientException("Error with MinioClient");
        }
    }


    @Override
    public InputStream getObject(String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioClientException("Error getting object from MinIO");
        }
    }

    @Override
    public List<String> listObjects(String prefix) {
        try {
            List<String> objects = new ArrayList<>();

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();

                if (!item.isDir()) {
                    objects.add(item.objectName());
                }
            }
            return objects;
        } catch (Exception e) {
            throw new MinioClientException("Error listing objects from MinIO");
        }
    }
}

