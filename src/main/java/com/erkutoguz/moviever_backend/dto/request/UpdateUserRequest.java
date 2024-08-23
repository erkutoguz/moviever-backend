package com.erkutoguz.moviever_backend.dto.request;

public record UpdateUserRequest(
        String firstname,
        String lastname,
        String password,
        String about,
        String pictureUrl) {

}
