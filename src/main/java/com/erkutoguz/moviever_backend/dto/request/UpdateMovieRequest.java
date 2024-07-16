package com.erkutoguz.moviever_backend.dto.request;

import com.erkutoguz.moviever_backend.model.Movie;

public class UpdateMovieRequest {
    private String title;
    private String director;
    private int releaseYear;
    private String pictureUrl;
    private double rating;

    public void updateMovie(Movie movie) {
        if (this.title != null) {
            movie.setTitle(this.title);
        }
        if (this.director != null) {
            movie.setDirector(this.director);
        }
        if (this.releaseYear != 0) {
            movie.setReleaseYear(this.releaseYear);
        }
        if (this.pictureUrl != null) {
            movie.setPictureUrl(this.pictureUrl);
        }
        if (this.rating != 0) {
            movie.setRating(this.rating);
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


}

//  updates.forEach((key, value) -> {
//        switch (key) {
//        case "username":
//        currentUser.setUsername((String) value);
//        break;
//        case "email":
//        currentUser.setEmail((String) value);
//        break;
//        case "firstname":
//        currentUser.setFirstname((String) value);
//        break;
//        case "lastname":
//        currentUser.setLastname((String) value);
//        break;
//        case "profile_picture":
//        currentUser.setProfilePicture((String) value);
//        break;
//default:
//        throw new IllegalArgumentException("Invalid field: " + key);
//            }
//                    });