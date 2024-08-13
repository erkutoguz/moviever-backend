package com.erkutoguz.moviever_backend.kafka.listener;

import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.model.UserDocument;
import com.erkutoguz.moviever_backend.service.ESMovieDocumentService;
import com.erkutoguz.moviever_backend.service.ESUserDocumentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ESListener {

    private final ESMovieDocumentService esMovieDocumentService;
    private final ESUserDocumentService esUserDocumentService;
    public ESListener(ESMovieDocumentService esMovieDocumentService,
                      ESUserDocumentService esUserDocumentService) {
        this.esMovieDocumentService = esMovieDocumentService;
        this.esUserDocumentService = esUserDocumentService;
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

    @KafkaListener(groupId = "esKafkaGroup", topics = "delete-user-document")
    public void consumeDeleteUserMessage(long userId) {
        esUserDocumentService.deleteUserDocument(userId);
    }



}
