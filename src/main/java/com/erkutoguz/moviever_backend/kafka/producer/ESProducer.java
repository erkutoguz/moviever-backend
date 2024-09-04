package com.erkutoguz.moviever_backend.kafka.producer;

import com.erkutoguz.moviever_backend.dto.request.UpdateMovieDocumentAndId;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieDocumentRequest;
import com.erkutoguz.moviever_backend.dto.request.UpdateUserDocumentStatusRequest;
import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.model.ReviewDocument;
import com.erkutoguz.moviever_backend.model.UserDocument;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ESProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public ESProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMovieDocument(MovieDocument movieDocument) {
        kafkaTemplate.send("add-movie-document",movieDocument);
    }
    public void sendUpdateMovieDocumentMessage(UpdateMovieDocumentRequest updateMovieDocument, Long movieId){
        kafkaTemplate.send("update-movie-document", new UpdateMovieDocumentAndId(updateMovieDocument, movieId));
    }

    public void sendDeleteMovieMessage(long movieId) {
        kafkaTemplate.send("delete-movie-document", movieId);
    }


    public void sendUserDocument(UserDocument userDocument) {
        kafkaTemplate.send("add-user-document", userDocument);
    }
    public void updateUserDocumentStatus(UpdateUserDocumentStatusRequest request) {
        kafkaTemplate.send("update-user-document-status", request);
    }
    public void sendDeleteUserMessage(long userId) {
        kafkaTemplate.send("delete-user-document", userId);
    }


    public void sendReviewDocument(ReviewDocument reviewDocument) {
        kafkaTemplate.send("add-review-document", reviewDocument);
    }
    public void sendDeleteReviewMessage(long reviewId) {
        kafkaTemplate.send("delete-review-document", reviewId);
    }


}
