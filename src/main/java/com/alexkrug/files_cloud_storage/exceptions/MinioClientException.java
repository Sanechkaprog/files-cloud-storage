package com.alexkrug.files_cloud_storage.exceptions;

public class MinioClientException extends RuntimeException {
    public MinioClientException(String message) {
        super(message);
    }
}
