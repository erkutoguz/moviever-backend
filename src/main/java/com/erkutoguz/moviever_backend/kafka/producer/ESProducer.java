package com.erkutoguz.moviever_backend.kafka.producer;

import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.model.UserDocument;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ESProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public ESProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMovieDocument(MovieDocument request) {
        kafkaTemplate.send("add-movie-document", request);
    }

    public void sendMovieDocumentList(List<MovieDocument> request) {
        kafkaTemplate.send("add-movie-document-list", request);
    }

    public void sendUserDocument(UserDocument request) {
        kafkaTemplate.send("add-user-document", request);
    }

    public void sendUserDocumentList(List<UserDocument> request) {
        kafkaTemplate.send("add-user-document-list", request);
    }

    public void sendDeleteMovieMessage(long movieId) {
        kafkaTemplate.send("delete-movie-document", movieId);
    }
    public void sendDeleteUserMessage(long userId) {
        kafkaTemplate.send("delete-user-document", userId);
    }

}
