package com.erkutoguz.moviever_backend.exception;

public class DuplicateResourceException extends RuntimeException{
    private int statusCode = 409;

    public DuplicateResourceException(String message) {
        super(message);
    }
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
