package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.model.MovieDocument;

import java.util.ArrayList;
import java.util.List;

public interface SortMovieDocumentById {
    private static List<MovieDocument> merge(List<MovieDocument> left, List<MovieDocument> right) {
        List<MovieDocument> result = new ArrayList<>();
        int l = 0;
        int r = 0;

        while (l < left.size() && r < right.size()) {
            if (left.get(l).getMovieId() > right.get(r).getMovieId()) {
                result.add(left.get(l));
                l++;
            } else {
                result.add(right.get(r));
                r++;
            }
        }

        while (l < left.size()) {
            result.add(left.get(l));
            l++;
        }
        while(r < right.size()) {
            result.add(right.get(r));
            r++;
        }

        return result;
    }
    static List<MovieDocument> sort(List<MovieDocument> list) {
        if(list.size() <= 1) {
            return list;
        }
        int middle = list.size() / 2;
        List<MovieDocument> left = new ArrayList<>(list.subList(0,middle));
        List<MovieDocument> right = new ArrayList<>(list.subList(middle, list.size()));

        return merge(sort(left), sort(right));
    }
}
