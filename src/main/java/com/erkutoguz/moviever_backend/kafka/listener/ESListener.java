package com.erkutoguz.moviever_backend.kafka.listener;

import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.model.ReviewDocument;
import com.erkutoguz.moviever_backend.model.UserDocument;
import com.erkutoguz.moviever_backend.service.ElasticsearchService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ESListener {

    private final ElasticsearchService elasticsearchService;
    public ESListener(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @KafkaListener(groupId = "esKafkaGroup", topics = "add-movie-document")
    public void consumeAddMovieMessage(MovieDocument movieDocument) {
        elasticsearchService.createMovieDocument(movieDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-movie-document")
    public void consumeDeleteMovieMessage(long movieId) {
        elasticsearchService.deleteMovieDocument(movieId);
    }

    @KafkaListener(groupId = "esKafkaGroup", topics = "add-user-document")
    public void consumeAddUserMessage(UserDocument userDocument) {
        elasticsearchService.createUserDocument(userDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "update-user-document-status")
    public void consumeUpdateUserStatusMessage(UpdateUserDocumentStatusRequest request) {
        elasticsearchService.updateUserDocumentByUserId(request.newStatus(), request.userId());
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-user-document")
    public void consumeDeleteUserMessage(long userId) {
        elasticsearchService.deleteUserDocument(userId);
    }


    @KafkaListener(groupId = "esKafkaGroup", topics = "add-review-document")
    public void consumeAddReviewMessage(ReviewDocument reviewDocument) {
        elasticsearchService.createReviewDocument(reviewDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-review-document")
    public void consumeDeleteReviewMessage(long reviewId) {
        elasticsearchService.deleteReviewDocument(reviewId);
    }


}
