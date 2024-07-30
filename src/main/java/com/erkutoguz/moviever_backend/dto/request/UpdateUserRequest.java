package com.erkutoguz.moviever_backend.dto.request;

public record UpdateUserRequest(String firstname, String lastname, String about, String pictureUrl) {
    public UpdateUserRequest{
        about = about != null ? about.trim() : null;
        pictureUrl = pictureUrl != null ? pictureUrl.trim() : null;
        firstname = firstname != null ? firstname.trim() : null;
        lastname = lastname != null ? lastname.trim() : null;
    }
}
