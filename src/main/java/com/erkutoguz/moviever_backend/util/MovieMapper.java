package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.MovieResponse;
import com.erkutoguz.moviever_backend.model.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieMapper {

    static MovieResponse map(Movie movie) {
        if (movie == null) return null;
        return new MovieResponse(movie.getId(), movie.getTitle(),
                movie.getReleaseYear(), movie.getRating(),movie.getPictureUrl(), movie.getLikeCount(), movie.getCategories());
    }
    static List<MovieResponse> map(List<Movie> movieList){
        if (movieList == null) return List.of();
        return movieList.stream().map(MovieMapper::map).toList();
    }
    static List<MovieResponse> map(Page<Movie> movieList){
        if (movieList == null) return List.of();
        return movieList.stream().map(MovieMapper::map).toList();
    }




}
