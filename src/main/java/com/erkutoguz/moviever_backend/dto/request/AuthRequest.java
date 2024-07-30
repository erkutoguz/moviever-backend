package com.erkutoguz.moviever_backend.dto.request;

public record AuthRequest(String username, String password) {
    public AuthRequest{
        username = username != null ? username.trim() : null;
        password = password != null ? password.trim() : null;
    }
}
