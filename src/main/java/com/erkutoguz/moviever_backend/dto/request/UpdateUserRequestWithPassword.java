package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestWithPassword(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
                String firstname,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastname,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 24, message = "Password must be between 6 and 24 characters")
        String password,

        @Size(max = 255, message = "About section cannot exceed 255 characters")
        String about
) {
}
