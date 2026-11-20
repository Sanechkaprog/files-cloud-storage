package com.alexkrug.files_cloud_storage.handler;

import com.alexkrug.files_cloud_storage.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.exceptions.FolderIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.exceptions.FolderNotExistsException;
import com.alexkrug.files_cloud_storage.exceptions.ResourceIsAlreadyExistException;
import com.alexkrug.files_cloud_storage.exceptions.ResourceNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.alexkrug.files_cloud_storage.resources")
public class ResourcesApiExceptionHandler {

    @ExceptionHandler(FolderNotExistsException.class)
    public ResponseEntity<ErrorResponse> folderNotExistException(FolderNotExistsException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(FolderIsAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> folderIsAlreadyExist(FolderIsAlreadyExistException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ResourceNotExistException.class)
    public ResponseEntity<ErrorResponse> resourceNotExist(ResourceNotExistException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> invalidPath(IllegalStateException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

   @ExceptionHandler(ResourceIsAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> resourceAlreadyExist(ResourceIsAlreadyExistException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.CONFLICT
        );
   }
}
