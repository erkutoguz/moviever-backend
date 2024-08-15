package com.erkutoguz.moviever_backend.exception;

public class AccessDeniedException extends RuntimeException{
    private int statusCode = 401;

    public AccessDeniedException(String message) {
        super(message);
    }
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
