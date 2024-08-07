package com.erkutoguz.moviever_backend.util;


import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.function.Supplier;

public class ESUtil {

    public static Supplier<Query> createAutoSuggestCriteriaQuery(String partialMovieName) {
//        Criteria criteria = new Criteria("title").contains(partialMovieName);
        Criteria criteria = new Criteria("title").fuzzy(partialMovieName);
        return () -> new CriteriaQuery(criteria);
    }


}
