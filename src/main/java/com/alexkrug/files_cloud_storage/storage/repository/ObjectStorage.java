package com.alexkrug.files_cloud_storage.storage.repository;

import io.minio.StatObjectResponse;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
public interface ObjectStorage {
    void createDirectory(String path);

    StatObjectResponse getStatObject(String path);

    void createFile(String path, InputStream is, Long size);
}
