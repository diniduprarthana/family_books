package com.familybooks.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer review for a book. Ownership is enforced via userId.
 */
public class Review {

    private String id;
    private String bookId;
    private String userId;
    private int rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(String id, String bookId, String userId, int rating,
                  String reviewText, LocalDateTime createdAt) {
        setId(id);
        setBookId(bookId);
        setUserId(userId);
        setRating(rating);
        setReviewText(reviewText);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Review ID is required.");
        }
        this.id = id.trim();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID is required for a review.");
        }
        this.bookId = bookId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required for a review.");
        }
        this.userId = userId.trim();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        if (reviewText == null || reviewText.isBlank()) {
            throw new IllegalArgumentException("Review text is required.");
        }
        this.reviewText = reviewText.trim();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isOwnedBy(String candidateUserId) {
        return Objects.equals(this.userId, candidateUserId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Review review)) {
            return false;
        }
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{id='" + id + "', bookId='" + bookId + "', rating=" + rating + "}";
    }
}
