package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Token can not be empty")
        String token,

        @NotBlank(message = "Password can not be empty")
        @Size(message = "Password must be between 6 and 24 characters", min = 6, max = 24)
        String newPassword) {
}
