package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.CreateMovieDocumentRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.repository.MovieDocumentRepository;
import com.erkutoguz.moviever_backend.util.ESUtil;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;


@Service
public class ESMovieDocumentService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final MovieDocumentRepository movieDocumentRepository;

    public ESMovieDocumentService(ElasticsearchOperations elasticsearchOperations,
                                  MovieDocumentRepository movieDocumentRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.movieDocumentRepository = movieDocumentRepository;
    }

    public MovieDocumentResponse insertMovieDocument(CreateMovieDocumentRequest request) {
        MovieDocument movieDocument = movieDocumentRequestToMovieDocument(request);
        movieDocumentRepository.save(movieDocument);
        return map(movieDocument);
    }

    public List<MovieDocumentResponse> searchMoviesAutoSuggest(String partialMovieName) throws IOException {
        Supplier<Query> query = ESUtil.createAutoSuggestCriteriaQuery(partialMovieName);
        SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(query.get(), MovieDocument.class);
        return extractMovieDocumentResponse(searchHits);
    }

    private MovieDocument movieDocumentRequestToMovieDocument(CreateMovieDocumentRequest request) {
        MovieDocument newMovieDocument = new MovieDocument();
        newMovieDocument.setMovieId(request.movieId());
        newMovieDocument.setTitle(request.title());
        newMovieDocument.setPosterUrl(request.posterUrl());
        return newMovieDocument;
    }

    private MovieDocumentResponse map(MovieDocument movieDocument) {
        return new MovieDocumentResponse(movieDocument.getMovieId(), movieDocument.getTitle(), movieDocument.getPosterUrl());
    }

    private List<MovieDocumentResponse> extractMovieDocumentResponse(SearchHits<MovieDocument> searchHits) {
        return searchHits.getSearchHits().stream().map(h ->
                new MovieDocumentResponse(h.getContent().getMovieId(),
                        h.getContent().getTitle(),
                        h.getContent().getPosterUrl()))
                .toList();
    }

}
