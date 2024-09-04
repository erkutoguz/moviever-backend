package com.erkutoguz.moviever_backend.dto.request;

import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;

import java.util.Set;

public record UpdateMovieDocumentRequest(String title, String posterUrl, int releaseYear, Set<CategoryResponse> categories) {
}
