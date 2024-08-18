package com.erkutoguz.moviever_backend.exception;

public class InternalServerException extends RuntimeException {
    private int statusCode = 500;

    public InternalServerException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
