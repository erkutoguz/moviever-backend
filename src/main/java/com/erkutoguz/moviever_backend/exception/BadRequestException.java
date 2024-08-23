package com.erkutoguz.moviever_backend.exception;

public class BadRequestException extends RuntimeException{
    private int statusCode = 400;

    public BadRequestException(String message) {
        super(message);
    }
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
