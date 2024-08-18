package com.erkutoguz.moviever_backend.kafka.listener;

import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.model.*;
import com.erkutoguz.moviever_backend.service.ESMovieDocumentService;
import com.erkutoguz.moviever_backend.service.ESUserDocumentService;
import com.erkutoguz.moviever_backend.service.EsReviewDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ESListener {

    private static final Logger log = LoggerFactory.getLogger(ESListener.class);
    private final ESMovieDocumentService esMovieDocumentService;
    private final ESUserDocumentService esUserDocumentService;
    private final EsReviewDocumentService esReviewDocumentService;
    public ESListener(ESMovieDocumentService esMovieDocumentService,
                      ESUserDocumentService esUserDocumentService,
                      EsReviewDocumentService esReviewDocumentService) {
        this.esMovieDocumentService = esMovieDocumentService;
        this.esUserDocumentService = esUserDocumentService;
        this.esReviewDocumentService = esReviewDocumentService;
    }

    @KafkaListener(groupId = "esKafkaGroup", topics = "add-movie-document")
    public void consumeAddMovieMessage(MovieDocument movieDocument) {
        esMovieDocumentService.insertMovieDocument(movieDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "add-movie-document-list")
    public void consumeAddMovieMessage(List<MovieDocument> movieDocumentList) {
        esMovieDocumentService.insertMultipleMovieDocuments(movieDocumentList);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-movie-document")
    public void consumeDeleteMovieMessage(long movieId) {
        esMovieDocumentService.deleteMovieDocument(movieId);
    }

    @KafkaListener(groupId = "esKafkaGroup", topics = "add-user-document")
    public void consumeAddUserMessage(UserDocument userDocument) {
        esUserDocumentService.insertUserDocument(userDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "add-user-document-list")
    public void consumeAddUserMessage(List<UserDocument> userDocumentList) {
        esUserDocumentService.insertMultipleUserDocuments(userDocumentList);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "update-user-document-status")
    public void consumeUpdateUserStatusMessage(UpdateUserDocumentStatusRequest request) {
        esUserDocumentService.updateUserDocumentStatus(request.userId(), request.newStatus());
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-user-document")
    public void consumeDeleteUserMessage(long userId) {
        esUserDocumentService.deleteUserDocument(userId);
    }

    @KafkaListener(groupId = "esKafkaGroup", topics = "add-review-document")
    public void consumeAddReviewMessage(ReviewDocument reviewDocument) {
        esReviewDocumentService.insertReviewDocument(reviewDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "add-review-document-list")
    public void consumeAddReviewMessage(List<ReviewDocument> reviewDocuments) {
        try{
            esReviewDocumentService.insertMultipleReviewDocuments(reviewDocuments);
        } catch(Exception e) {
            log.error(e.toString());
        }

    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-review-document")
    public void consumeDeleteReviewMessage(long reviewId) {
        esReviewDocumentService.deleteReviewDocument(reviewId);
    }


}
