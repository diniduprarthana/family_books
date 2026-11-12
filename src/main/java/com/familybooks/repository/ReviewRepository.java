package com.familybooks.repository;

import com.familybooks.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    List<Review> findAll();

    Optional<Review> findById(String id);

    List<Review> findByBookId(String bookId);

    List<Review> findByUserId(String userId);

    Review save(Review review);

    void deleteById(String id);

    void deleteByBookId(String bookId);

    void deleteByUserId(String userId);

    long count();
}
