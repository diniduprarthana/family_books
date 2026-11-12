package com.familybooks.service;

import com.familybooks.exception.InvalidOperationException;
import com.familybooks.exception.ResourceNotFoundException;
import com.familybooks.model.Review;
import com.familybooks.repository.BookRepository;
import com.familybooks.repository.ReviewRepository;
import com.familybooks.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    public Review create(String bookId, String userId, int rating, String reviewText) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found: " + bookId);
        }
        Review review = new Review(
                IdGenerator.next("REV"),
                bookId,
                userId,
                rating,
                reviewText,
                LocalDateTime.now()
        );
        return reviewRepository.save(review);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll().stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .toList();
    }

    public List<Review> findByBookId(String bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .toList();
    }

    public List<Review> findRecent(int limit) {
        return findAll().stream().limit(limit).toList();
    }

    public Review getById(String id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
    }

    public double averageRating(String bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        OptionalDouble average = reviews.stream().mapToInt(Review::getRating).average();
        return average.orElse(0.0);
    }

    public Review update(String reviewId, String requesterUserId, boolean isAdmin,
                         int rating, String reviewText) {
        Review review = getById(reviewId);
        assertCanModify(review, requesterUserId, isAdmin);
        review.setRating(rating);
        review.setReviewText(reviewText);
        return reviewRepository.save(review);
    }

    public void delete(String reviewId, String requesterUserId, boolean isAdmin) {
        Review review = getById(reviewId);
        assertCanModify(review, requesterUserId, isAdmin);
        reviewRepository.deleteById(reviewId);
    }

    public long count() {
        return reviewRepository.count();
    }

    private void assertCanModify(Review review, String requesterUserId, boolean isAdmin) {
        if (!isAdmin && !review.isOwnedBy(requesterUserId)) {
            throw new InvalidOperationException("You may only modify your own reviews.");
        }
    }
}
