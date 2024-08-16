package com.erkutoguz.moviever_backend.exception;

public class UnverifiedEmailException extends RuntimeException{
    private int statusCode = 403;

    public UnverifiedEmailException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
