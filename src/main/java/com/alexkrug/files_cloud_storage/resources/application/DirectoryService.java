package com.alexkrug.files_cloud_storage.resources.application;

import io.minio.MinioClient;
import org.springframework.stereotype.Service;

@Service
public class DirectoryService {
    private final MinioClient minioClient;

    public DirectoryService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }




}
