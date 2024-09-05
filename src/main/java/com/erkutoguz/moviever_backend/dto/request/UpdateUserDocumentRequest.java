package com.erkutoguz.moviever_backend.dto.request;

public record UpdateUserDocumentRequest(Long userId, String firstName, String lastName) {
}
