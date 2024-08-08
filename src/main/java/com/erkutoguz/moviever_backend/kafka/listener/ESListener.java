package com.erkutoguz.moviever_backend.kafka.listener;


import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.service.ESMovieDocumentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ESListener {

    private final ESMovieDocumentService esMovieDocumentService;
    public ESListener(ESMovieDocumentService esMovieDocumentService) {
        this.esMovieDocumentService = esMovieDocumentService;
    }

    @KafkaListener(groupId = "esKafkaGroup", topics = "add-movie-document")
    public void consumeAddMovieMessage(MovieDocument movieDocument) {
        esMovieDocumentService.insertMovieDocument(movieDocument);
    }
    @KafkaListener(groupId = "esKafkaGroup", topics = "add-movie-document-list")
    public void consumeAddMovieMessage(List<MovieDocument> movieDocumentList) {
        esMovieDocumentService.insertMultipleMovieDocuments(movieDocumentList);
    }


}
