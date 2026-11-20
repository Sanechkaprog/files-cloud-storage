package com.alexkrug.files_cloud_storage.service;

import com.alexkrug.files_cloud_storage.exceptions.MinioClientException;
import com.alexkrug.files_cloud_storage.exceptions.StatsObjectUnavailbaleGetException;
import com.alexkrug.files_cloud_storage.repository.ObjectStorage;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
    public List<String> listObjects(String prefix, boolean isNotDirectory) {
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
                if (isNotDirectory) {
                    if (!item.isDir()) {
                        objects.add(item.objectName());
                    }
                } else {
                    objects.add(item.objectName());
                }
            }
            return objects;
        } catch (Exception e) {
            throw new MinioClientException("Error listing objects from MinIO");
        }
    }

    @Override
    public void deleteFile(String path) {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            throw new MinioClientException("Error with MinioClient");
        }
    }

    @Override
    public List<String> deleteFolder(String prefix) {
        String safePrefix = prefix.endsWith("/") ? prefix : prefix + "/";
        List<String> objects = listObjects(safePrefix, false);
        List<DeleteObject> deleteObjects = objects
                .stream()
                .map(DeleteObject::new)
                .toList();
        if (!deleteObjects.isEmpty()) {
            var result = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .objects(deleteObjects)
                            .build()
            );
            for (Result<DeleteError> error : result) {
                try {
                    DeleteError err = error.get();
                    log.error("Unable to delete {} : {}" ,err.objectName(), err.message());
                    objects.remove(err.objectName());
                } catch (Exception ex) {
                    throw new MinioClientException("Error with MinioClient");
                }
            }
        }

        return objects;
    }

    @Override
    public void moveFile(String from, String to) {
        CopySource copySource = CopySource
                .builder()
                .bucket(BUCKET_NAME)
                .object(from)
                .build();

        CopyObjectArgs copyObjectArgs = CopyObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object(to)
                .source(copySource)
                .build();

        try {
            minioClient.copyObject(copyObjectArgs);
        } catch (Exception e) {
            throw new MinioClientException("Error with MinioClient");
        }
    }
}

