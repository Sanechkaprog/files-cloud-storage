package com.alexkrug.files_cloud_storage.common.exchandler;

import com.alexkrug.files_cloud_storage.auth.exceptions.AuthException;
import com.alexkrug.files_cloud_storage.auth.exceptions.InvalidCredentialsException;
import com.alexkrug.files_cloud_storage.common.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.storage.exception.MinioClientException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> serverInternalExceptions(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorResponse>> invalidPatternExc(ConstraintViolationException ex) {
        List<ErrorResponse> errorResponses = ex
                .getConstraintViolations()
                .stream()
                .map(violation -> new ErrorResponse(violation.getMessage()))
                .toList();
        return new ResponseEntity<>(
                errorResponses,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MinioClientException.class)
    public ResponseEntity<ErrorResponse> minioClientException(MinioClientException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> invalidCredentials(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
}

