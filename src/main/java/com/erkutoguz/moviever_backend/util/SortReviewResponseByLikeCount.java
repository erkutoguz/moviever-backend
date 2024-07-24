package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.ReviewResponse;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public interface SortReviewResponseByLikeCount {

   static List<ReviewResponse> merge(List<ReviewResponse> left, List<ReviewResponse> right) {
        List<ReviewResponse> arr = new ArrayList<>();
        int l = 0;
        int r = 0;

        while (l < left.size() && r < right.size()) {
            if(left.get(l).likeCount() == 0 && right.get(r).likeCount() == 0) {
                long leftTime = System.currentTimeMillis() - left.get(l).createdAt().toInstant(ZoneOffset.UTC).toEpochMilli();
                long rightTime = System.currentTimeMillis() - right.get(r).createdAt().toInstant(ZoneOffset.UTC).toEpochMilli();
                if(leftTime > rightTime) {
                    arr.add(right.get(r));
                    r++;
                }else {
                    arr.add(left.get(l));
                    l++;
                }
            } else {
                if (left.get(l).likeCount() >= right.get(r).likeCount()) {
                    arr.add(left.get(l));
                    l++;
                } else {
                    arr.add(right.get(r));
                    r++;
                }
            }

        }

        while (l < left.size()) {
            arr.add(left.get(l));
            l++;
        }

        while (r < right.size()) {
            arr.add(right.get(r));
            r++;
        }

        return arr;
    }

    static List<ReviewResponse> mergeSort(List<ReviewResponse> arr) {
        if (arr.size() <= 1) {
            return arr;
        }

        int mid = arr.size() / 2;
        List<ReviewResponse> left = new ArrayList<>(arr.subList(0, mid));
        List<ReviewResponse> right = new ArrayList<>(arr.subList(mid, arr.size()));

        return merge(mergeSort(left), mergeSort(right));
    }

    static List<ReviewResponse> sortByLike(List<ReviewResponse> reviewResponseList) {
        return mergeSort(reviewResponseList);
    }
}
