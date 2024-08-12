package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.CategoryType;
import com.erkutoguz.moviever_backend.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

        Page<Movie> findAllByOrderByIdDesc(Pageable pageable);

        @Query("SELECT m from Movie m JOIN m.watchlists w WHERE w.id = :watchlistId")
        Page<Movie> findByWatchlistId(@Param("watchlistId") Long watchlistId, Pageable pageable);

        @Query("SELECT m FROM Movie m JOIN m.categories c WHERE c.categoryName = :categoryName")
        Page<Movie> findByCategoryName(@Param("categoryName") CategoryType categoryName, Pageable pageable);
}
