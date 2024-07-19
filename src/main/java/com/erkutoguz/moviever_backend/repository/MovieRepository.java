package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.CategoryType;
import com.erkutoguz.moviever_backend.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

//    List<Movie> findAll(Sort sort);
        Page<Movie> findAll(Pageable pageable);

        @Query("SELECT m FROM Movie m JOIN m.categories c WHERE c.categoryName = :categoryName")
        Page<Movie> findByCategoryName(@Param("categoryName") CategoryType categoryName, Pageable pageable);
}
