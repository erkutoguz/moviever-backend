package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendResetPasswordEmailRequest(
        @NotBlank(message = "Email can not be empty")
        @Email(message = "Invalid email")
        String email) {
}
