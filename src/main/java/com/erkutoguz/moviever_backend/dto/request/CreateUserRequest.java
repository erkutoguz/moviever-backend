package com.erkutoguz.moviever_backend.dto.request;

public record CreateUserRequest(String username, String email, String password, String firstname, String lastname) {
}
