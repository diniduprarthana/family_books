package com.familybooks.repository.file;

import com.familybooks.model.Review;
import com.familybooks.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Text-file persistence for reviews.
 * Format: id|bookId|userId|rating|reviewText|createdAt
 */
@Repository
public class FileReviewRepository implements ReviewRepository {

    private static final Logger log = LoggerFactory.getLogger(FileReviewRepository.class);
    private static final String FILE_NAME = "reviews.txt";

    private final FileStorageSupport storage;

    public FileReviewRepository(FileStorageSupport storage) {
        this.storage = storage;
        this.storage.ensureFile(FILE_NAME);
    }

    @Override
    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        for (String line : storage.readLines(FILE_NAME)) {
            parseLine(line).ifPresent(reviews::add);
        }
        return reviews;
    }

    @Override
    public Optional<Review> findById(String id) {
        return findAll().stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public List<Review> findByBookId(String bookId) {
        return findAll().stream().filter(r -> r.getBookId().equals(bookId)).toList();
    }

    @Override
    public List<Review> findByUserId(String userId) {
        return findAll().stream().filter(r -> r.getUserId().equals(userId)).toList();
    }

    @Override
    public Review save(Review review) {
        List<Review> reviews = findAll();
        boolean replaced = false;
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(review.getId())) {
                reviews.set(i, review);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            reviews.add(review);
        }
        persist(reviews);
        return review;
    }

    @Override
    public void deleteById(String id) {
        List<Review> reviews = findAll();
        reviews.removeIf(r -> r.getId().equals(id));
        persist(reviews);
    }

    @Override
    public void deleteByBookId(String bookId) {
        List<Review> reviews = findAll();
        reviews.removeIf(r -> r.getBookId().equals(bookId));
        persist(reviews);
    }

    @Override
    public void deleteByUserId(String userId) {
        List<Review> reviews = findAll();
        reviews.removeIf(r -> r.getUserId().equals(userId));
        persist(reviews);
    }

    @Override
    public long count() {
        return findAll().size();
    }

    private void persist(List<Review> reviews) {
        List<String> lines = new ArrayList<>();
        for (Review review : reviews) {
            lines.add(String.join(FileStorageSupport.FIELD_SEPARATOR,
                    storage.escape(review.getId()),
                    storage.escape(review.getBookId()),
                    storage.escape(review.getUserId()),
                    String.valueOf(review.getRating()),
                    storage.escape(review.getReviewText()),
                    review.getCreatedAt().toString()
            ));
        }
        storage.writeLines(FILE_NAME, lines);
    }

    private Optional<Review> parseLine(String line) {
        try {
            String[] p = storage.splitFields(line, 6);
            return Optional.of(new Review(
                    storage.unescape(p[0]),
                    storage.unescape(p[1]),
                    storage.unescape(p[2]),
                    Integer.parseInt(p[3].trim()),
                    storage.unescape(p[4]),
                    LocalDateTime.parse(p[5].trim())
            ));
        } catch (Exception e) {
            log.warn("Skipping corrupted review record: {}", line);
            return Optional.empty();
        }
    }
}
