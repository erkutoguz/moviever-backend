package com.erkutoguz.moviever_backend.util;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;
import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.MovieDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface MovieDocumentMapper {
    static MovieDocument map(Movie movie) {
        return new MovieDocument(movie.getId(),movie.getTitle(),movie.getPictureUrl(),
                movie.getReleaseYear(),movie.getCategories()
                .stream().map(
                        c -> new CategoryResponse(c.getCategoryName().toString()))
                .collect(Collectors.toSet()));
    }
    static MovieDocumentResponse map(MovieDocument movieDocument) {
        return new MovieDocumentResponse(movieDocument.getId(), movieDocument.getTitle(), movieDocument.getPosterUrl(), movieDocument.getReleaseYear(), movieDocument.getCategories());
    }

    static List<MovieDocumentResponse> map(List<Hit<MovieDocument>> hits) {
        List<MovieDocumentResponse> list = new ArrayList<>();
        hits.forEach(h -> list.add(map(h.source())));
        return list;
    }
}
