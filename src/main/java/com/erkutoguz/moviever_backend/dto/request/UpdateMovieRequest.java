package com.erkutoguz.moviever_backend.dto.request;

import com.erkutoguz.moviever_backend.model.CategoryType;
import com.erkutoguz.moviever_backend.validation.DateValidation;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record UpdateMovieRequest(
        @NotBlank(message = "Title can not be empty")
        String title,

        @NotBlank(message = "Director can not be empty")
        String director,

        @DateValidation(message = "Invalid release year")
        @NotNull(message = "Release year cannot be empty")
        int releaseYear,

        @NotBlank(message = "Description can not be empty")
        String description,

        @NotBlank(message = "Trailer can not be empty")
        String trailerUrl,

        @DecimalMin(value = "0.0",  message = "Rating must be greater than 0")
        @DecimalMax(value = "10.0", message = "Rating must be less than or equal to 10")
        @NotNull(message = "Rating cannot be empty")
        double rating,

        @NotEmpty(message = "Categories can not be empty")
        Set<CategoryType> categories) {
}
