package com.erkutoguz.moviever_backend.kafka.producer;


import com.erkutoguz.moviever_backend.dto.request.CreateMovieRequest;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.MovieDocument;
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

    public void sendDeleteMessage(Long movieId) {
        kafkaTemplate.send("delete-movie-document", movieId);
    }

}
