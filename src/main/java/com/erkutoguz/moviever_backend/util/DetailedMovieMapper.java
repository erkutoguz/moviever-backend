package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.MovieResponseWithDetails;
import com.erkutoguz.moviever_backend.model.Movie;

public interface DetailedMovieMapper {

    static MovieResponseWithDetails map(Movie movie,boolean isUserLiked) {
        if (movie == null) return null;
        return new MovieResponseWithDetails(movie.getId(),movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear(), movie.getRating(), movie.getPictureUrl(), movie.getTrailerUrl(),
                movie.getLikeCount(), isUserLiked, movie.getCategories());
    }

}
