package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.AdminReviewResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.ReviewDocument;
import com.erkutoguz.moviever_backend.repository.ReviewDocumentRepository;
import com.erkutoguz.moviever_backend.util.ESUtil;
import com.erkutoguz.moviever_backend.util.ReviewDocumentMapper;
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
import java.util.function.Supplier;


@Service
public class EsReviewDocumentService {
    private final ReviewDocumentRepository reviewDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public EsReviewDocumentService(ReviewDocumentRepository reviewDocumentRepository, ElasticsearchOperations elasticsearchOperations) {
        this.reviewDocumentRepository = reviewDocumentRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public void insertReviewDocument(ReviewDocument reviewDocument) {
        reviewDocumentRepository.save(reviewDocument);
    }

    public void insertMultipleReviewDocuments(List<ReviewDocument> reviewDocumentList) {
        reviewDocumentRepository.saveAll(reviewDocumentList);
    }

    public void deleteReviewDocument(Long reviewId) {
        ReviewDocument reviewDocument = reviewDocumentRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review Document not found"));
        reviewDocumentRepository.delete(reviewDocument);
    }

    //TODO cache at buralara da
    public Map<String, Object> searchReviewAutoSuggest(String partialInput, int page, int size) throws IOException {
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> map = new HashMap<>();
        Page<ReviewDocument> reviewDocuments = reviewDocumentRepository
                .searchByUsernameOrMovieNameOrReview(partialInput, pageable);
        List<AdminReviewResponse> reviews = ReviewDocumentMapper.mapReviewDocument(reviewDocuments.getContent());
        map.put("totalItems", reviewDocuments.getTotalElements());
        map.put("totalPages", reviewDocuments.getTotalPages());
        map.put("reviews", reviews);
        return map;
    }
    public Map<String, Object> searchReviewAutoSuggestV2(String partialInput, int page, int size) throws IOException {
        Supplier<Query> query = ESUtil.createAutoSuggestCriteriaQueryForReview(partialInput);
        Pageable pageable = PageRequest.of(page, size);
        Query pageableQuery = query.get().setPageable(pageable);
        SearchHits<ReviewDocument> searchHits = elasticsearchOperations.search(pageableQuery, ReviewDocument.class);
        Page<ReviewDocument> reviewDocumentPage = SearchHitSupport.searchPageFor(searchHits, pageable).map(SearchHit::getContent);

        Map<String, Object> map = new HashMap<>();

        List<AdminReviewResponse> reviews = ReviewDocumentMapper.mapReviewDocument(reviewDocumentPage.getContent());
        map.put("totalItems", reviewDocumentPage.getTotalElements());
        map.put("totalPages", reviewDocumentPage.getTotalPages());
        map.put("reviews", reviews);
        return map;
    }
}
