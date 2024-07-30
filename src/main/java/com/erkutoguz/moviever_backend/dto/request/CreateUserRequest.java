package com.erkutoguz.moviever_backend.dto.request;

public record CreateUserRequest(String username, String email,
                                String password, String firstname, String lastname) {
    public CreateUserRequest{
        username = username != null ? username.trim() : null;
        email = email != null ? email.trim() : null;
        password = password != null ? password.trim() : null;
        firstname = firstname != null ? firstname.trim() : null;
        lastname = lastname != null ? lastname.trim() : null;
    }
}
