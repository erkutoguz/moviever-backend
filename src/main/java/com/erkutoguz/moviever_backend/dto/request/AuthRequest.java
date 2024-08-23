package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Username can not be empty")
        @Size(message = "Username must be between 3 and 50 characters", min = 3, max = 50) String username,


        @NotBlank(message = "Password can not be empty")
        @Size(message = "Password must be between 6 and 24 characters", min = 6, max = 24)
        String password) {

}
