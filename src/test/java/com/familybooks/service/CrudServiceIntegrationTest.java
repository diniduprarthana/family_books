package com.familybooks.service;

import com.familybooks.exception.DuplicateResourceException;
import com.familybooks.exception.InvalidOperationException;
import com.familybooks.model.Book;
import com.familybooks.model.Category;
import com.familybooks.model.EBook;
import com.familybooks.model.PrintedBook;
import com.familybooks.model.Review;
import com.familybooks.model.User;
import com.familybooks.repository.file.FileStorageSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for all four CRUD modules against real text-file storage.
 */
class CrudServiceIntegrationTest {

    @TempDir
    Path tempDir;

    private UserService userService;
    private CategoryService categoryService;
    private BookService bookService;
    private ReviewService reviewService;
    private FileStorageSupport storage;

    @BeforeEach
    void setUp() {
        storage = new FileStorageSupport(tempDir.toString());
        var userRepo = new com.familybooks.repository.file.FileUserRepository(storage);
        var categoryRepo = new com.familybooks.repository.file.FileCategoryRepository(storage);
        var bookRepo = new com.familybooks.repository.file.FileBookRepository(storage);
        var reviewRepo = new com.familybooks.repository.file.FileReviewRepository(storage);
        var encoder = new BCryptPasswordEncoder();

        userService = new UserService(userRepo, reviewRepo, encoder);
        categoryService = new CategoryService(categoryRepo, bookRepo);
        bookService = new BookService(bookRepo, categoryRepo, reviewRepo);
        reviewService = new ReviewService(reviewRepo, bookRepo);
    }

    @Test
    void userCrud_registerLoginFieldsUpdateDelete() {
        User user = userService.register("dinidu", "dinidu@example.com", "Secret1", "Dinidu Student");
        assertNotNull(user.getId());
        assertTrue(user.getPasswordHash().startsWith("$2a$") || user.getPasswordHash().startsWith("$2b$"));

        User loaded = userService.getById(user.getId());
        assertEquals("dinidu", loaded.getUsername());

        userService.updateProfile(user.getId(), "Dinidu Updated", "dinidu2@example.com");
        assertEquals("Dinidu Updated", userService.getById(user.getId()).getFullName());

        userService.changePassword(user.getId(), "Secret1", "Secret2");
        assertThrows(InvalidOperationException.class,
                () -> userService.changePassword(user.getId(), "wrong", "Secret3"));

        userService.deleteAccount(user.getId());
        assertThrows(Exception.class, () -> userService.getById(user.getId()));
    }

    @Test
    void userRegistration_rejectsDuplicates() {
        userService.register("amaya", "amaya@example.com", "Secret1", "Amaya");
        assertThrows(DuplicateResourceException.class,
                () -> userService.register("amaya2", "amaya@example.com", "Secret1", "Other"));
    }

    @Test
    void categoryAndBookCrud_withPolymorphism() {
        Category fiction = categoryService.create("Fiction", "Stories");
        PrintedBook printed = (PrintedBook) bookService.createPrinted(
                "Quiet Shelf", "A. Author", "978111", fiction.getId(),
                "A printed novel", new BigDecimal("1500"), "/images/books/placeholder.jpg", 5, 220);
        EBook ebook = (EBook) bookService.createEBook(
                "Digital Shelf", "B. Author", "978222", fiction.getId(),
                "An ebook", new BigDecimal("900"), "/images/books/placeholder.jpg", "EPUB", "Immediate");

        assertEquals("PRINTED", printed.getBookType());
        assertTrue(printed.getAvailabilityDescription().contains("stock")
                || printed.getAvailabilityDescription().contains("copies")
                || printed.getAvailabilityDescription().contains("Only"));
        assertEquals("EBOOK", ebook.getBookType());
        assertTrue(ebook.getAvailabilityDescription().toLowerCase().contains("digital"));

        List<String> descriptions = bookService.describeAvailabilityForAll();
        assertEquals(2, descriptions.size());
        assertTrue(descriptions.stream().anyMatch(d -> d.contains("PRINTED")));
        assertTrue(descriptions.stream().anyMatch(d -> d.contains("EBOOK")));

        Book updated = bookService.update(printed.getId(), "Quiet Shelf Revised", "A. Author", "978111",
                fiction.getId(), "Updated", new BigDecimal("1600"), printed.getCoverImage(),
                "PRINTED", 2, 230, null, null);
        assertEquals("Quiet Shelf Revised", updated.getTitle());

        bookService.delete(ebook.getId());
        assertEquals(1, bookService.count());
    }

    @Test
    void categoryDelete_blockedWhenBooksExist() {
        Category category = categoryService.create("Mystery", "Suspense");
        bookService.createPrinted("Cipher", "Author", "978333", category.getId(),
                "desc", new BigDecimal("1000"), null, 3, 100);
        assertThrows(InvalidOperationException.class, () -> categoryService.delete(category.getId()));
    }

    @Test
    void reviewCrud_ownershipEnforced() {
        Category category = categoryService.create("Romance", "Love");
        Book book = bookService.createPrinted("Promises", "Author", "978444", category.getId(),
                "desc", new BigDecimal("1200"), null, 4, 180);
        User owner = userService.register("owner", "owner@example.com", "Secret1", "Owner");
        User other = userService.register("other", "other@example.com", "Secret1", "Other");

        Review review = reviewService.create(book.getId(), owner.getId(), 5, "Wonderful literary escape.");
        assertEquals(5.0, reviewService.averageRating(book.getId()), 0.01);

        reviewService.update(review.getId(), owner.getId(), false, 4, "Still lovely after a second reading.");
        assertEquals(4, reviewService.getById(review.getId()).getRating());

        assertThrows(InvalidOperationException.class,
                () -> reviewService.update(review.getId(), other.getId(), false, 1, "Nope"));
        assertThrows(InvalidOperationException.class,
                () -> reviewService.delete(review.getId(), other.getId(), false));

        reviewService.delete(review.getId(), owner.getId(), false);
        assertEquals(0, reviewService.count());
    }

    @Test
    void filePersistence_survivesReloadedRepositories() {
        Category category = categoryService.create("History", "Past");
        bookService.createPrinted("Ink", "Author", "978555", category.getId(),
                "desc", new BigDecimal("2000"), null, 7, 300);
        userService.register("persist", "persist@example.com", "Secret1", "Persist User");

        var userRepo2 = new com.familybooks.repository.file.FileUserRepository(storage);
        var bookRepo2 = new com.familybooks.repository.file.FileBookRepository(storage);
        var categoryRepo2 = new com.familybooks.repository.file.FileCategoryRepository(storage);

        assertEquals(1, userRepo2.findAll().size());
        assertEquals(1, bookRepo2.findAll().size());
        assertEquals(1, categoryRepo2.findAll().size());
        assertEquals("PRINTED", bookRepo2.findAll().getFirst().getBookType());
    }

    @Test
    void invalidInputs_areRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.register("ab", "bad", "1", ""));
        Category category = categoryService.create("Science", "Science");
        assertThrows(IllegalArgumentException.class,
                () -> bookService.createPrinted("", "A", "1", category.getId(), "d",
                        new BigDecimal("-1"), null, -1, 0));
    }
}
