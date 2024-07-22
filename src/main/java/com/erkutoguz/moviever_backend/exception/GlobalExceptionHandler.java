package com.erkutoguz.moviever_backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFoundException(ResourceNotFoundException ex) {
        ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(exception, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTokenException(InvalidTokenException ex) {
        ExceptionResponse exception = new ExceptionResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
    }
}
