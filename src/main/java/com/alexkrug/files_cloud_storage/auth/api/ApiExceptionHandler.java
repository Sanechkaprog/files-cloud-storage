package com.alexkrug.files_cloud_storage.auth.api;

import com.alexkrug.files_cloud_storage.auth.api.dto.ErrorResponse;
import com.alexkrug.files_cloud_storage.auth.exceptions.InvalidCredentialsException;
import com.alexkrug.files_cloud_storage.auth.exceptions.NicknameIsTakenException;
import com.alexkrug.files_cloud_storage.auth.exceptions.SignOutWithoutAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> invalidSize(MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error ->
                        new ErrorResponse(error.getDefaultMessage()))
                .toList();

        return new ResponseEntity<>(
                errors,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NicknameIsTakenException.class)
    public ResponseEntity<ErrorResponse> nicknameIsTaken(NicknameIsTakenException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({InvalidCredentialsException.class, SignOutWithoutAuthException.class})
    public ResponseEntity<ErrorResponse> invalidCredentials(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> serverInternalExceptions(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
