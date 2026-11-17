package com.alexkrug.files_cloud_storage.storage.exception;

public class MinioClientException extends RuntimeException {
    public MinioClientException(String message) {
        super(message);
    }
}
