package com.familybooks.config;

import com.familybooks.model.EBook;
import com.familybooks.model.PrintedBook;
import com.familybooks.service.BookService;
import com.familybooks.service.CategoryService;
import com.familybooks.service.ReviewService;
import com.familybooks.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds demo data once if storage files are empty / missing records.
 * Idempotent: will not duplicate existing IDs or the admin account.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final CategoryService categoryService;
    private final BookService bookService;
    private final ReviewService reviewService;

    public DataInitializer(UserService userService,
                           CategoryService categoryService,
                           BookService bookService,
                           ReviewService reviewService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategories();
        seedBooks();
        seedReviews();
        log.info("Family Books data initialization complete. Books={}, Users={}, Categories={}, Reviews={}",
                bookService.count(), userService.count(), categoryService.count(), reviewService.count());
    }

    private void seedUsers() {
        userService.createAdminIfAbsent("admin", "admin@familybooks.lk", "Admin@123", "Family Books Admin");
        if (userService.findByUsername("amaya").isEmpty()) {
            userService.register("amaya", "amaya@example.com", "Customer1", "Amaya Perera");
        }
        if (userService.findByUsername("nimal").isEmpty()) {
            userService.register("nimal", "nimal@example.com", "Customer1", "Nimal Silva");
        }
    }

    private void seedCategories() {
        categoryService.createWithIdIfAbsent("CAT-FICTION", "Fiction", "Imaginative stories and literary novels.");
        categoryService.createWithIdIfAbsent("CAT-NONFICTION", "Non-Fiction", "Essays, ideas, and real-world narratives.");
        categoryService.createWithIdIfAbsent("CAT-ROMANCE", "Romance", "Love stories across eras and settings.");
        categoryService.createWithIdIfAbsent("CAT-MYSTERY", "Mystery", "Whodunits, suspense, and detective tales.");
        categoryService.createWithIdIfAbsent("CAT-BIOGRAPHY", "Biography", "Lives that shaped history and culture.");
        categoryService.createWithIdIfAbsent("CAT-HISTORY", "History", "Past worlds, empires, and turning points.");
        categoryService.createWithIdIfAbsent("CAT-SCIENCE", "Science", "Discovery, nature, and scientific thought.");
        categoryService.createWithIdIfAbsent("CAT-CHILDREN", "Children's Books", "Stories for young readers and families.");
    }

    private void seedBooks() {
        if (bookService.count() > 0) {
            return;
        }

        bookService.saveSeed(new PrintedBook(
                "BK-0001", "The Quiet Library", "Eleanor Hart", "9780001000001",
                "CAT-FICTION",
                "A luminous novel about a coastal bookshop that becomes a refuge for forgotten stories.",
                new BigDecimal("2450.00"), "/images/covers/quiet-library.svg", 12, 328));

        bookService.saveSeed(new PrintedBook(
                "BK-0002", "Letters from Colombo", "Ravi Mendis", "9780001000002",
                "CAT-NONFICTION",
                "Warm essays on city life, monsoon mornings, and the books we inherit from home.",
                new BigDecimal("1890.00"), "/images/covers/letters-colombo.svg", 8, 256));

        bookService.saveSeed(new PrintedBook(
                "BK-0003", "Moonlit Promises", "Sofia Laurent", "9780001000003",
                "CAT-ROMANCE",
                "Two archivists fall in love while restoring a century-old manuscript.",
                new BigDecimal("2100.00"), "/images/covers/moonlit-promises.svg", 15, 312));

        bookService.saveSeed(new PrintedBook(
                "BK-0004", "The Galle Face Cipher", "Jonas Reed", "9780001000004",
                "CAT-MYSTERY",
                "A missing first edition draws a detective into Colombo's literary underworld.",
                new BigDecimal("2750.00"), "/images/covers/galle-face-cipher.svg", 4, 401));

        bookService.saveSeed(new PrintedBook(
                "BK-0005", "Ink & Empire", "Priya Nadesan", "9780001000005",
                "CAT-HISTORY",
                "How printing presses reshaped identity across the Indian Ocean world.",
                new BigDecimal("3200.00"), "/images/covers/ink-empire.svg", 6, 448));

        bookService.saveSeed(new PrintedBook(
                "BK-0006", "Starlight Botany", "Dr. Helen Crowe", "9780001000006",
                "CAT-SCIENCE",
                "A graceful introduction to plants that navigate by night and moonlight.",
                new BigDecimal("2650.00"), "/images/covers/starlight-botany.svg", 10, 288));

        bookService.saveSeed(new PrintedBook(
                "BK-0007", "The Little Book Keeper", "Maya Fonseka", "9780001000007",
                "CAT-CHILDREN",
                "A picture-led adventure about a child who organises a magical family shelf.",
                new BigDecimal("1250.00"), "/images/covers/little-book-keeper.svg", 20, 48));

        bookService.saveSeed(new PrintedBook(
                "BK-0008", "Portrait of a Reader", "Amelia Vos", "9780001000008",
                "CAT-BIOGRAPHY",
                "The life of a legendary bookseller who believed every home needed a reading corner.",
                new BigDecimal("2990.00"), "/images/covers/portrait-reader.svg", 3, 360));

        bookService.saveSeed(new EBook(
                "BK-0009", "Digital Hearth", "Theo Lang", "9780001000009",
                "CAT-NONFICTION",
                "Essays on building a personal library in the age of screens.",
                new BigDecimal("990.00"), "/images/covers/digital-hearth.svg", "EPUB", "Immediate"));

        bookService.saveSeed(new EBook(
                "BK-0010", "Night Shelf Tales", "Lina Ortega", "9780001000010",
                "CAT-FICTION",
                "Short stories best read after midnight, delivered as a digital keepsake.",
                new BigDecimal("850.00"), "/images/covers/night-shelf.svg", "PDF", "Immediate"));
    }

    private void seedReviews() {
        if (reviewService.count() > 0) {
            return;
        }
        var amaya = userService.findByUsername("amaya");
        var nimal = userService.findByUsername("nimal");
        if (amaya.isPresent()) {
            reviewService.create("BK-0001", amaya.get().getId(), 5,
                    "Felt like wandering through a beloved family bookshop. Absolutely beautiful.");
            reviewService.create("BK-0003", amaya.get().getId(), 4,
                    "Tender romance with a literary heart. Finished it in one evening.");
        }
        if (nimal.isPresent()) {
            reviewService.create("BK-0004", nimal.get().getId(), 5,
                    "Clever mystery set against Colombo streets. Could not put it down.");
            reviewService.create("BK-0009", nimal.get().getId(), 4,
                    "Thoughtful e-book on modern reading habits. Practical and warm.");
        }
    }
}
