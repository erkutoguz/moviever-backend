package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;
import com.erkutoguz.moviever_backend.model.Category;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface CategoryMapper {
    static CategoryResponse map(Category category) {
        if (category == null) return null;
        return new CategoryResponse(category.getCategoryName().toString());
    }
    static Set<CategoryResponse> map(Set<Category> categories) {
        if (categories == null) return null;
        return categories.stream().map(CategoryMapper::map).collect(Collectors.toSet());
    }

}
