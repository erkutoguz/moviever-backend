package com.erkutoguz.moviever_backend.dto.response;

public record MovieResponse(Long id, String title, int releaseYear, double rating, String posterUrl) {
}
