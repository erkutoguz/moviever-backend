package com.erkutoguz.moviever_backend.dto.log;

public class ErrorLog extends Log {

    private int errorCode;
    private String errorMessage;
    private String username;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ErrorLog{" +
                "Error Code => " + errorCode +
                ", Error Message => '" + errorMessage + '\'' +
                ", Username => '" + username + '\'' +
                ", Method Signature => '" + methodSignature + '\'' +
                ", Request Url => '" + requestUrl + '\'' +
                ", Request Method => '" + requestMethod + '\'' +
                ", Ip Address => '" + ipAddress + '\'' +
                ", User Agent => '" + userAgent + '\'' +
                '}';
    }
}
