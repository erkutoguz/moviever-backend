package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByMovieId(Long movieId, Pageable pageable);
    Page<Review> findAllByOrderByIdDesc(Pageable pageable);

}
