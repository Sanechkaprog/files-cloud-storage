package com.alexkrug.files_cloud_storage.exceptions;

public class ResourceNotExistException extends RuntimeException {
    public ResourceNotExistException(String message) {
        super(message);
    }
}
