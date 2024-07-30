package com.erkutoguz.moviever_backend.dto.request;

public record RefreshTokenRequest(String refreshToken) {
    public RefreshTokenRequest{
        refreshToken = refreshToken != null ? refreshToken.trim() : null;
    }
}
