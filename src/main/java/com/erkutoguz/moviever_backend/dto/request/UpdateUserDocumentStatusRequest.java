package com.erkutoguz.moviever_backend.dto.request;

public record UpdateUserDocumentStatusRequest(Long userId, boolean newStatus) {
}
