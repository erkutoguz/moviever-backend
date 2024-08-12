package com.erkutoguz.moviever_backend.util;

import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.function.Supplier;

public class ESUtil {

    public static Supplier<Query> createAutoSuggestCriteriaQueryForMovie(String partialMovieName) {
        if (partialMovieName == null || partialMovieName.isEmpty()) {
            throw new IllegalArgumentException("Invalid movie name");
        }
        Criteria criteria = new Criteria("title");
        for(String s : partialMovieName.split("[\\s.,]+")){
            criteria.contains(s);
        }
        return () -> new CriteriaQuery(criteria);
    }





}
