package com.erkutoguz.moviever_backend.exception;

public class ResourceNotFoundException extends RuntimeException{
    private int statusCode = 404;
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
