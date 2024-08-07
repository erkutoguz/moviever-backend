package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MovieDocumentRepository extends ElasticsearchRepository<MovieDocument, String> {
}
