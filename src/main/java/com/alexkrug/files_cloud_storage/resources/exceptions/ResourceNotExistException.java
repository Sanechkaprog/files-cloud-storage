package com.alexkrug.files_cloud_storage.resources.exceptions;

public class ResourceNotExistException extends RuntimeException {
    public ResourceNotExistException(String message) {
        super(message);
    }
}
