package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.CategoryResponse;
import com.erkutoguz.moviever_backend.model.Category;

import java.util.List;

public interface CategoryMapper {
    static CategoryResponse map(Category category) {
        if (category == null) return null;
        return new CategoryResponse(category.getCategoryName().toString());
    }
    static List<CategoryResponse> map(List<Category> categories) {
        if (categories == null) return null;
        return categories.stream().map(CategoryMapper::map).toList();
    }

}
