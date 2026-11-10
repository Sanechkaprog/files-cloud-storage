package com.alexkrug.files_cloud_storage.auth.exceptions;

public class NicknameIsTakenException extends RuntimeException {
    public NicknameIsTakenException(String message) {
        super(message);
    }
}
