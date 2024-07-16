package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.MovieResponseWithDetails;
import com.erkutoguz.moviever_backend.model.Movie;

public interface DetailedMovieMapper {

    static MovieResponseWithDetails map(Movie movie) {
        if (movie == null) return null;
        return new MovieResponseWithDetails(movie.getId(),movie.getTitle(),
                movie.getDirector(), ReviewMapper.map(movie.getReviews()),
                movie.getReleaseYear(), movie.getRating(), movie.getPictureUrl(),
                movie.getLikeCount(), movie.getCategories());
    }

}
