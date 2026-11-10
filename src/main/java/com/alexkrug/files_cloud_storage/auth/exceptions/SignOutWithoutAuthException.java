package com.alexkrug.files_cloud_storage.auth.exceptions;

public class SignOutWithoutAuthException extends RuntimeException {
    public SignOutWithoutAuthException(String message) {
        super(message);
    }
}
