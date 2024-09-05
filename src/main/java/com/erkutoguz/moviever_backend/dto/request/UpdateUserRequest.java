package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(@NotBlank(message = "First name is required")
                                @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
                                String firstname,

                                @NotBlank(message = "Last name is required")
                                @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
                                String lastname,
                                String about) {
}
