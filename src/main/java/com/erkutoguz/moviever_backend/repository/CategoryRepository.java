package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.Category;
import com.erkutoguz.moviever_backend.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(CategoryType categoryName);
}
