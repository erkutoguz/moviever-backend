package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.AdminUserResponse;
import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.model.UserDocument;
import com.erkutoguz.moviever_backend.repository.MovieDocumentRepository;
import com.erkutoguz.moviever_backend.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;


@Service
public class ESMovieDocumentService {

    private static final Logger log = LoggerFactory.getLogger(ESMovieDocumentService.class);
    private final ElasticsearchOperations elasticsearchOperations;
    private final MovieDocumentRepository movieDocumentRepository;
    public ESMovieDocumentService(ElasticsearchOperations elasticsearchOperations,
                                  MovieDocumentRepository movieDocumentRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.movieDocumentRepository = movieDocumentRepository;
    }

    public void insertMovieDocument(MovieDocument movieDocument) {
        movieDocumentRepository.save(movieDocument);
    }

    public void insertMultipleMovieDocuments(List<MovieDocument> movieDocumentList) {
        movieDocumentRepository.saveAll(movieDocumentList);
    }

    public Map<String, Object> searchMoviesAutoSuggest(String partialMovieName, String categoryName, int page, int size)
            throws IOException {
        Supplier<Query> query = ESUtil.createAutoSuggestCriteriaQueryForMovie(partialMovieName.trim(),categoryName);

        Pageable pageable = PageRequest.of(page, size);
        Query pageableQuery = query.get().setPageable(pageable);

        SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(pageableQuery, MovieDocument.class);

        Page<MovieDocument> movieDocumentPage = SearchHitSupport.searchPageFor(searchHits, pageable).map(SearchHit::getContent);
        Map<String, Object> map = new HashMap<>();

        map.put("totalItems", movieDocumentPage.getTotalElements());
        map.put("totalPages", movieDocumentPage.getTotalPages());

        List<MovieDocument> sortedList = SortMovieDocumentById.sort(movieDocumentPage.getContent());
        map.put("movies", mapMovieDocument(sortedList));
        return map;
    }


    private List<MovieDocumentResponse> mapMovieDocument(List<MovieDocument> movieDocumentList) {
        return movieDocumentList.stream().map(md ->
                new MovieDocumentResponse(md.getMovieId(), md.getTitle(), md.getPosterUrl(),md.getReleaseYear(), md.getCategories())
        ).toList();
    }

    public void deleteMovieDocument(Long movieId) {
        MovieDocument movieDocument = movieDocumentRepository.findByMovieId(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie Document not found"));
        movieDocumentRepository.delete(movieDocument);
    }


}
