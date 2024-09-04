package com.erkutoguz.moviever_backend.model;

public class ReviewDocument {
    private Long id;
    private String username;
    private Long userId;
    private Long movieId;
    private String movieName;
    private String review;
    private String createdAt;
    private int likeCount;

    public ReviewDocument() {
    }

    public ReviewDocument(Long id, String username, Long userId,
                          Long movieId, String movieName, String review,
                          String createdAt, int likeCount) {
        this.id = id;
        this.username = username;
        this.movieId = movieId;
        this.userId = userId;
        this.movieName = movieName;
        this.review = review;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
