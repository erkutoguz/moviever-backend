package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Token can not be empty")
        String refreshToken) {
}
