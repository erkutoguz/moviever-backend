package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface MovieDocumentRepository extends ElasticsearchRepository<MovieDocument, String> {
    Optional<MovieDocument> findByMovieId(long movieId);
}
