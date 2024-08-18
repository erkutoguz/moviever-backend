package com.erkutoguz.moviever_backend.dto.log;

import java.util.List;

public class UserLog extends Log{
    private String username;
    private int responseCode;
    private long executionTime;
    private List<String> args;

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "Username => '" + username + '\'' +
                ", Response Code => " + responseCode +
                ", Execution Time => " + executionTime +
                ", Method Signature => '" + methodSignature + '\'' +
                ", Arguments => " + args +
                ", Request Url => '" + requestUrl + '\'' +
                ", Request Method => '" + requestMethod + '\'' +
                ", Ip Address => '" + ipAddress + '\'' +
                ", User Agent => '" + userAgent + '\'' +
                '}';
    }
}
