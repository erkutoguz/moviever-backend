package com.erkutoguz.moviever_backend.repository;

import com.erkutoguz.moviever_backend.model.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, Long> {
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
          "email": {
            "value": "*?0*",
            "case_insensitive": true
          }
        }
      },
      {
        "wildcard": {
            "firstName": {
              "value": "*?0*",
              "case_insensitive": true
            }
          }
      },
      {
        "wildcard": {
          "lastName": {
            "value": "*?0*",
            "case_insensitive": true
          }
        }
      }
    ]
  }
}
""")
    Page<UserDocument> searchByUsernameOrEmailOrFirstNameOrLastName(String input, Pageable pageable);
}
