package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminUserResponse;

import java.util.ArrayList;
import java.util.List;

public interface SortUserDocumentById {

    private static List<AdminUserResponse> merge(List<AdminUserResponse> left, List<AdminUserResponse> right) {
        List<AdminUserResponse> result = new ArrayList<>();
        int l = 0;
        int r = 0;

        while (l < left.size() && r < right.size()) {
            if(left.get(l).id() <= right.get(r).id()) {
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
        while (r < right.size()) {
            result.add(right.get(r));
            r++;
        }
        return result;
    }

    static List<AdminUserResponse> sort(List<AdminUserResponse> list) {
        if (list.size() <= 1) {
            return list;
        }
        int middle = list.size() / 2;
        List<AdminUserResponse> left = list.subList(0,middle);
        List<AdminUserResponse> right = list.subList(middle,list.size());
        return merge(sort(left), sort(right));
    }
}
