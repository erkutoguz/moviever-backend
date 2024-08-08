package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.MovieDocument;

import java.util.ArrayList;
import java.util.List;

public interface MovieDocumentMapper {

    static MovieDocument map(Movie request) {
        MovieDocument movieDocument = new MovieDocument();
        movieDocument.setTitle(request.getTitle());
        movieDocument.setMovieId(request.getId());
        movieDocument.setPosterUrl(request.getPictureUrl());
        return movieDocument;
    }

    static List<MovieDocument> map(List<Movie> request) {
        List<MovieDocument> movieDocumentList = new ArrayList<>();
        for (Movie m : request) {
            movieDocumentList.add(map(m));
        }
        return movieDocumentList;
    }

}
