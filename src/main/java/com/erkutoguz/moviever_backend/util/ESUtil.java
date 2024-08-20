package com.erkutoguz.moviever_backend.util;

import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.function.Supplier;

public class ESUtil {

    public static Supplier<Query> createAutoSuggestCriteriaQueryForMovie(String partialMovieName, String categoryName) {
        if (partialMovieName == null || partialMovieName.isEmpty()) {
            throw new IllegalArgumentException("Invalid movie name");
        }
        Criteria criteria = new Criteria("title");
        for(String s : partialMovieName.split("[\\s.,]+")){
            criteria.contains(s);
        }
        if (categoryName != null && !categoryName.isEmpty()) {
            criteria = criteria.and(new Criteria("categories.categoryType").is(categoryName));
        }

        Criteria query = criteria;
        return () -> new CriteriaQuery(query);
    }
    public static Supplier<Query> createAutoSuggestCriteriaQueryForReview(String partialInput) {
        if (partialInput == null || partialInput.isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }
        Criteria usernameCriteria = new Criteria("username");
        for(String s : partialInput.split("[\\s.,]+")){
            usernameCriteria.contains(s);
        }
        Criteria movieNameCriteria = new Criteria("movieName");
        for(String s :  partialInput.split("[\\s.,]+")) {
            movieNameCriteria.contains(s);
        }
        Criteria reviewCriteria = new Criteria("review");
        for(String s :  partialInput.split("[\\s.,]+")) {
            reviewCriteria.contains(s);
        }

        Criteria finalCriteria = movieNameCriteria.or(usernameCriteria).or(reviewCriteria);

        return () -> new CriteriaQuery(finalCriteria);
    }





}
