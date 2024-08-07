package com.erkutoguz.moviever_backend.dto.request;

public record CreateMovieDocumentRequest(Long movieId, String title, String posterUrl) {
}
