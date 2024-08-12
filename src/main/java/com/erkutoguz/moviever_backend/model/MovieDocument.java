package com.erkutoguz.moviever_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "movies")
@Setting(settingPath = "static/es-movie-settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private long movieId;

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer")
    private String title;

    @Field(type = FieldType.Text)
    private String posterUrl;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

}
