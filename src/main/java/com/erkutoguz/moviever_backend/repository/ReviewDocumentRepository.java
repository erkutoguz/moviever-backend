package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.ReviewDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ReviewDocumentRepository extends ElasticsearchRepository<ReviewDocument, Long> {

    @Query("""
{
  "bool": {
    "should": [
      {
        "wildcard": {
          "username": {
            "value": "*?0*",
            "case_insensitive": true
          }
        }
      },
      {
        "wildcard": {
          "movieName": {
            "value": "*?0*",
            "case_insensitive": true
          }
        }
      },
      {
        "wildcard": {
            "review": {
              "value": "*?0*",
              "case_insensitive": true
            }
          }
      }
    ]
  }
}
""")
    Page<ReviewDocument> searchByUsernameOrMovieNameOrReview(String input, Pageable pageable);
}
