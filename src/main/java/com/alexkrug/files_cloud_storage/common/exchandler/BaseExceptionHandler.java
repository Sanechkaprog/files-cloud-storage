package com.alexkrug.files_cloud_storage.common.exchandler;

import com.alexkrug.files_cloud_storage.common.dto.ErrorResponse;
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
}

