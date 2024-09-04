package com.erkutoguz.moviever_backend.model;

import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;

import java.util.Set;

public class MovieDocument {

    private Long id;
    private String title;
    private String posterUrl;
    private int releaseYear;
    private Set<CategoryResponse> categories;

    public MovieDocument() {
    }

    public MovieDocument(Long id, String title,
                         String posterUrl, int releaseYear,
                         Set<CategoryResponse> categories) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.releaseYear = releaseYear;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Set<CategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryResponse> categories) {
        this.categories = categories;
    }
}
