package com.alexkrug.files_cloud_storage.resources.exceptions;

public class FolderNotExistsException extends RuntimeException {
    public FolderNotExistsException(String message) {
        super(message);
    }
}
