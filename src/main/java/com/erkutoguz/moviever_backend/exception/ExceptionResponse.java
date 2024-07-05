package com.erkutoguz.moviever_backend.exception;

public class ExceptionResponse {

    private String message;
    private int statusCode;

    public ExceptionResponse(String message, int statusCode) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
