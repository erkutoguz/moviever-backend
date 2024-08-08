package com.erkutoguz.moviever_backend.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.repository.MovieDocumentRepository;
import com.erkutoguz.moviever_backend.util.ESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;


@Service
public class ESMovieDocumentService {

    private static final Logger log = LoggerFactory.getLogger(ESMovieDocumentService.class);
    private final ElasticsearchOperations elasticsearchOperations;
    private final MovieDocumentRepository movieDocumentRepository;
    private final ElasticsearchClient elasticsearchClient;
    public ESMovieDocumentService(ElasticsearchOperations elasticsearchOperations,
                                  MovieDocumentRepository movieDocumentRepository, ElasticsearchClient elasticsearchClient) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.movieDocumentRepository = movieDocumentRepository;
        this.elasticsearchClient = elasticsearchClient;
    }

    public void insertMovieDocument(MovieDocument movieDocument) {
        movieDocumentRepository.save(movieDocument);
    }

    public void insertMultipleMovieDocuments(List<MovieDocument> movieDocumentList) {
        movieDocumentRepository.saveAll(movieDocumentList);
    }

    public List<MovieDocumentResponse> searchMoviesAutoSuggest(String partialMovieName) throws IOException {
        Supplier<Query> query = ESUtil.createAutoSuggestCriteriaQuery(partialMovieName);
        SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(query.get(), MovieDocument.class);
        return extractMovieDocumentResponse(searchHits);
    }


    private List<MovieDocumentResponse> extractMovieDocumentResponse(SearchHits<MovieDocument> searchHits) {
        return searchHits.getSearchHits().stream().map(h ->
                new MovieDocumentResponse(h.getContent().getMovieId(),
                        h.getContent().getTitle(),
                        h.getContent().getPosterUrl()))
                .toList();
    }

}
