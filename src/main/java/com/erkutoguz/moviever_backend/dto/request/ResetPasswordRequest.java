package com.erkutoguz.moviever_backend.dto.request;

public record ResetPasswordRequest(String token, String newPassword) {
}
