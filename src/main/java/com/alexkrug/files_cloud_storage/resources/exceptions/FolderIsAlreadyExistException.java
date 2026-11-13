package com.alexkrug.files_cloud_storage.resources.exceptions;

public class FolderIsAlreadyExistException extends RuntimeException {
    public FolderIsAlreadyExistException(String message) {
        super(message);
    }
}
