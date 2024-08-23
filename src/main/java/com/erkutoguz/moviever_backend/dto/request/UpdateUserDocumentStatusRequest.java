package com.erkutoguz.moviever_backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserDocumentStatusRequest(
        @NotNull(message = "User id cannot be empty")
        Long userId,
        boolean newStatus) {
}
