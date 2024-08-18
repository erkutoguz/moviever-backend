package com.erkutoguz.moviever_backend.dto.log;

public class AuthLog extends Log{
    private String username;
    private long executionTime;
    private int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        return "AuthLog{" +
                "Username => '" + username + '\'' +
                ", Method Signature => '" + methodSignature + '\'' +
                ", Request Url => '" + requestUrl + '\'' +
                ", Execution Time => " + executionTime +
                ", Response Code => " + responseCode +
                ", Request Method => '" + requestMethod + '\'' +
                ", Ip Address => '" + ipAddress + '\'' +
                ", User Agent => '" + userAgent + '\'' +
                '}';
    }
}
