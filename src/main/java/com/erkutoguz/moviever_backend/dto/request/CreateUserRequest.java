package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Username can not be empty")
        @Size(message = "Username must be between 3 and 24 characters", min = 3, max = 24)
        String username,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email can not be empty")
        String email,

        @NotBlank(message = "Password can not be empty")
        @Size(message = "Password must be between 6 and 24 characters", min = 6, max = 24)
        String password,

        @NotBlank(message = "Firstname can not be empty")
        @Size(message = "Firstname must be between 3 and 24 characters", min = 3, max = 24)
        String firstname,

        @NotBlank(message = "Lastname can not be empty")
        @Size(message = "Lastname must be between 3 and 24 characters", min = 3, max = 24)
        String lastname) {
}
