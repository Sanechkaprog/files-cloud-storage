package com.alexkrug.files_cloud_storage.storage.repository;

import io.minio.StatObjectResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectStorage {
    void createDirectory(String path);

    StatObjectResponse getStatObject(String path);
}
