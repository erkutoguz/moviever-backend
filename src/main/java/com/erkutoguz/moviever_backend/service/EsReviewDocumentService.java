package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.response.AdminReviewResponse;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.ReviewDocument;
import com.erkutoguz.moviever_backend.repository.ReviewDocumentRepository;
import com.erkutoguz.moviever_backend.util.ReviewDocumentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EsReviewDocumentService {
    private final ReviewDocumentRepository reviewDocumentRepository;

    public EsReviewDocumentService(ReviewDocumentRepository reviewDocumentRepository) {
        this.reviewDocumentRepository = reviewDocumentRepository;
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
}
