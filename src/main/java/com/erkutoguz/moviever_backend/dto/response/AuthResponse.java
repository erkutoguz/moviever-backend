package com.erkutoguz.moviever_backend.dto.response;

public record AuthResponse(String username, String accessToken, String refreshToken) {
}
