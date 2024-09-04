package com.erkutoguz.moviever_backend.service;

import aj.org.objectweb.asm.Type;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.erkutoguz.moviever_backend.dto.request.UpdateMovieDocumentRequest;
import com.erkutoguz.moviever_backend.dto.response.MovieDocumentResponse;
import com.erkutoguz.moviever_backend.model.MovieDocument;
import com.erkutoguz.moviever_backend.model.ReviewDocument;
import com.erkutoguz.moviever_backend.model.UserDocument;
import com.erkutoguz.moviever_backend.util.MovieDocumentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;
    public ElasticsearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public Map<String, Object> searchMoviesByQueryAndCategory(String partialInput, String categoryType, int page, int size) {
        SearchRequest request = SearchRequest.of(s -> s
                .index("movies")
                .query(q -> q
                                .bool(b -> b
                                        .must(sh -> sh
                                                .wildcard(w -> w
                                                        .field("title")
                                                        .value("*"+partialInput+"*")
                                                )
                                        ).must(m -> m
                                                .match(ma -> ma
                                                        .field("categories.categoryType")
                                                        .query(categoryType)
                                                )
                                        )
                                )
                        )
        );
        Map<String, Object> map = new HashMap<>();

        try {
            var results = elasticsearchClient.search(request, MovieDocument.class);
            Pageable pageable = PageRequest.of(page, size);
            List<MovieDocumentResponse> list = MovieDocumentMapper.map(results.hits().hits());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), list.size());
            Page<MovieDocumentResponse> moviePage = new PageImpl<>(list.subList(start, end), pageable, list.size());
            map.put("movies", moviePage.getContent());
            map.put("totalItems", moviePage.getTotalElements());
            map.put("totalPages", moviePage.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> searchMoviesByQuery(String partialInput, int page, int size) {
        SearchRequest request = SearchRequest.of(s -> s
                .index("movies")
                .query(q -> q
                        .bool(b -> b
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("title")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                        )
                )
        );
        Map<String, Object> map = new HashMap<>();

        try {
            var results = elasticsearchClient.search(request, MovieDocument.class);
            Pageable pageable = PageRequest.of(page, size);
            List<MovieDocumentResponse> list = MovieDocumentMapper.map(results.hits().hits());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), list.size());
            Page<MovieDocumentResponse> moviePage = new PageImpl<>(list.subList(start, end), pageable, list.size());
            map.put("movies", moviePage.getContent());
            map.put("totalItems", moviePage.getTotalElements());
            map.put("totalPages", moviePage.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> searchUsersByQuery(String partialInput, int page, int size) {
        SearchRequest request = SearchRequest.of(s -> s
                .index("users")
                .query(q -> q
                        .bool(b -> b
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("firstName")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("lastName")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("email")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("username")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                        )
                )
        );


        Map<String, Object> map = new HashMap<>();

        try {
            var results = elasticsearchClient.search(request, UserDocument.class);
            Pageable pageable = PageRequest.of(page, size);
            List<UserDocument> list = results.hits().hits().stream().map(Hit::source).toList();
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), list.size());
            Page<UserDocument> moviePage = new PageImpl<>(list.subList(start, end), pageable, list.size());
            map.put("users", moviePage.getContent());
            map.put("totalItems", moviePage.getTotalElements());
            map.put("totalPages", moviePage.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> searchReviewByQuery(String partialInput, int page, int size) {
        SearchRequest request = SearchRequest.of(s -> s
                .index("reviews")
                .query(q -> q
                        .bool(b -> b
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("review")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("movieName")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                                .should(sh -> sh
                                        .wildcard(w -> w
                                                .field("username")
                                                .value("*"+partialInput+"*")
                                        )
                                )
                        )
                )
        );


        Map<String, Object> map = new HashMap<>();

        try {
            var results = elasticsearchClient.search(request, ReviewDocument.class);
            Pageable pageable = PageRequest.of(page, size);
            List<ReviewDocument> list = results.hits().hits().stream().map(Hit::source).toList();
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), list.size());
            Page<ReviewDocument> moviePage = new PageImpl<>(list.subList(start, end), pageable, list.size());
            map.put("reviews", moviePage.getContent());
            map.put("totalItems", moviePage.getTotalElements());
            map.put("totalPages", moviePage.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;

    }

    public void createMovieDocument(MovieDocument movieDocument){
        IndexRequest<MovieDocument> request = IndexRequest.of(i -> i
                .id(movieDocument.getId().toString())
                .index("movies")
                .document(movieDocument));

        try {
            elasticsearchClient.index(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMovieDocument(UpdateMovieDocumentRequest request, Long movieId) {
        Map<String, JsonData> map = new HashMap<>();
        map.put("posterUrl", JsonData.of(request.posterUrl()));
        map.put("releaseYear", JsonData.of(request.releaseYear()));
        map.put("title", JsonData.of(request.title()));
        map.put("categories", JsonData.of(request.categories()));
        UpdateRequest<MovieDocument, Boolean> updateRequest = UpdateRequest.of(u -> u
                .script(s -> s
                        .inline(is -> is
                                .params(map)
                                .source("ctx._source.posterUrl=params.posterUrl; ctx._source.title=params.title; ctx._source.releaseYear=params.releaseYear; ctx._source.categories=params.categories")))
                .index("movies")
                .id(movieId.toString())
        );
        try {
            elasticsearchClient.update(updateRequest, Type.getType(UserDocument.class).getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMovieDocument(long movieId) {
        DeleteRequest request = DeleteRequest.of(d -> d.index("movies").id(String.valueOf(movieId)));
        DeleteByQueryRequest deleteReviews = DeleteByQueryRequest.of(d -> d
                .index("reviews")
                .query(q ->
                        q.term(t -> t
                                .field("movieId")
                                .value(movieId))));

        try {
            elasticsearchClient.delete(request);
            elasticsearchClient.deleteByQuery(deleteReviews);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createUserDocument(UserDocument userDocument) {
        IndexRequest<UserDocument> request = IndexRequest.of(i -> i
                .index("users")
                .id(userDocument.getId().toString())
                .document(userDocument));
        try {
            elasticsearchClient.index(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserDocumentByUserId(boolean newStatus, Long userId) {
        UpdateRequest<UserDocument, Boolean> request = UpdateRequest.of(u -> u
                .script(s -> s
                        .inline(is -> is
                                .params(Collections.singletonMap("newStatus", JsonData.of(newStatus)))
                                .source("ctx._source.enabled=params.newStatus")))
                .index("users")
                .id(userId.toString())
        );
        try {
            elasticsearchClient.update(request, Type.getType(UserDocument.class).getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUserDocument(long userId) {
        DeleteRequest request = DeleteRequest.of(d -> d.index("users").id(String.valueOf(userId)));
        DeleteByQueryRequest deleteUserReviews = DeleteByQueryRequest.of(d -> d
                .index("reviews")
                .query(q ->
                        q.term(t -> t
                                .value(userId)
                                .field("userId")
                        )
                )
        );
        try {
            elasticsearchClient.delete(request);
            elasticsearchClient.deleteByQuery(deleteUserReviews);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createReviewDocument(ReviewDocument reviewDocument) {
        IndexRequest<ReviewDocument> request = IndexRequest.of(i -> i
                .index("reviews")
                .id(reviewDocument.getId().toString())
                .document(reviewDocument));
        try {
            elasticsearchClient.index(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteReviewDocument(long reviewId) {
        DeleteRequest request = DeleteRequest.of(d -> d.index("reviews").id(String.valueOf(reviewId)));
        try {
            elasticsearchClient.delete(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
