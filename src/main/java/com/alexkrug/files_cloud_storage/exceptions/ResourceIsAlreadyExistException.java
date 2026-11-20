package com.alexkrug.files_cloud_storage.exceptions;

public class ResourceIsAlreadyExistException extends RuntimeException {
    public ResourceIsAlreadyExistException(String message) {
        super(message);
    }
}
