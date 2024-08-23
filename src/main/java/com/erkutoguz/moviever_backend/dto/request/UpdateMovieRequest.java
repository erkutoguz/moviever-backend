package com.erkutoguz.moviever_backend.dto.request;

import com.erkutoguz.moviever_backend.validation.DateValidation;
import jakarta.validation.constraints.*;

public record UpdateMovieRequest(
        @NotBlank(message = "Title can not be empty")
        String title,

        @NotBlank(message = "Director can not be empty")
        String director,

        @NotNull(message = "Release year cannot be empty")
        @DateValidation(message = "Invalid release year")
        int releaseYear,

        @NotBlank(message = "Poster can not be empty")
        String pictureUrl,

        @NotNull(message = "Rating cannot be empty")
        @DecimalMin(value = "0.0",  message = "Rating must be greater than 0")
        @DecimalMax(value = "10.0", message = "Rating must be less than or equal to 10")
        double rating) {
}
