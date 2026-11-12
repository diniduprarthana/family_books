package com.familybooks.controller;

import com.familybooks.model.Book;
import com.familybooks.model.UserRole;
import com.familybooks.service.BookCoverStorageService;
import com.familybooks.service.BookService;
import com.familybooks.service.CategoryService;
import com.familybooks.service.ReviewService;
import com.familybooks.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final BookCoverStorageService bookCoverStorageService;

    public AdminController(BookService bookService,
                           CategoryService categoryService,
                           UserService userService,
                           ReviewService reviewService,
                           BookCoverStorageService bookCoverStorageService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.bookCoverStorageService = bookCoverStorageService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("bookCount", bookService.count());
        model.addAttribute("userCount", userService.count());
        model.addAttribute("categoryCount", categoryService.count());
        model.addAttribute("reviewCount", reviewService.count());
        model.addAttribute("recentBooks", bookService.findRecent(5));
        model.addAttribute("recentReviews", reviewService.findRecent(5));
        model.addAttribute("lowStockBooks", bookService.findLowStock(5));
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("adminSection", "overview");
        return "admin/dashboard";
    }

    // ---------- Books ----------

    @GetMapping("/books")
    public String books(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("books", bookService.searchFilterSort(q, null, "title"));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Manage Books");
        model.addAttribute("adminSection", "books");
        return "admin/books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("defaultCover", BookCoverStorageService.PLACEHOLDER);
        model.addAttribute("pageTitle", "Add Book");
        model.addAttribute("adminSection", "books");
        return "admin/book-form";
    }

    @PostMapping("/books")
    public String createBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam String isbn,
                             @RequestParam String categoryId,
                             @RequestParam String description,
                             @RequestParam BigDecimal price,
                             @RequestParam(required = false) MultipartFile coverFile,
                             @RequestParam(required = false, defaultValue = "false") boolean removeCover,
                             @RequestParam String bookType,
                             @RequestParam(required = false) Integer stockQuantity,
                             @RequestParam(required = false) Integer pageCount,
                             @RequestParam(required = false) String digitalFormat,
                             @RequestParam(required = false) String downloadAccess,
                             RedirectAttributes redirectAttributes) {
        String storedCover = null;
        try {
            String coverImage = resolveNewCover(coverFile, removeCover);
            storedCover = bookCoverStorageService.isManagedUpload(coverImage) ? coverImage : null;

            if ("EBOOK".equalsIgnoreCase(bookType)) {
                bookService.createEBook(title, author, isbn, categoryId, description, price,
                        coverImage, digitalFormat, downloadAccess);
            } else {
                bookService.createPrinted(title, author, isbn, categoryId, description, price,
                        coverImage,
                        stockQuantity != null ? stockQuantity : 0,
                        pageCount != null ? pageCount : 1);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Book created successfully.");
        } catch (RuntimeException ex) {
            if (storedCover != null) {
                bookCoverStorageService.deleteUploadFile(storedCover);
            }
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/books/new";
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable String id, Model model) {
        Book book = bookService.getById(id);
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("defaultCover", BookCoverStorageService.PLACEHOLDER);
        model.addAttribute("pageTitle", "Edit Book");
        model.addAttribute("adminSection", "books");
        return "admin/book-form";
    }

    @PostMapping("/books/{id}")
    public String updateBook(@PathVariable String id,
                             @RequestParam String title,
                             @RequestParam String author,
                             @RequestParam String isbn,
                             @RequestParam String categoryId,
                             @RequestParam String description,
                             @RequestParam BigDecimal price,
                             @RequestParam(required = false) MultipartFile coverFile,
                             @RequestParam(required = false, defaultValue = "false") boolean removeCover,
                             @RequestParam String bookType,
                             @RequestParam(required = false) Integer stockQuantity,
                             @RequestParam(required = false) Integer pageCount,
                             @RequestParam(required = false) String digitalFormat,
                             @RequestParam(required = false) String downloadAccess,
                             RedirectAttributes redirectAttributes) {
        String storedCover = null;
        String previousCover = null;
        try {
            Book existing = bookService.getById(id);
            previousCover = existing.getCoverImage();
            String coverImage = resolveUpdatedCover(previousCover, coverFile, removeCover);
            if (bookCoverStorageService.isManagedUpload(coverImage)
                    && !coverImage.equals(previousCover)) {
                storedCover = coverImage;
            }

            bookService.update(id, title, author, isbn, categoryId, description, price, coverImage,
                    bookType, stockQuantity, pageCount, digitalFormat, downloadAccess);

            if (previousCover != null && !previousCover.equals(coverImage)) {
                bookCoverStorageService.deleteIfUnusedUpload(previousCover);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully.");
        } catch (RuntimeException ex) {
            if (storedCover != null) {
                bookCoverStorageService.deleteUploadFile(storedCover);
            }
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/books/" + id + "/edit";
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.getById(id);
            String coverImage = book.getCoverImage();
            bookService.delete(id);
            bookCoverStorageService.deleteIfUnusedUpload(coverImage);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/books";
    }

    private String resolveNewCover(MultipartFile coverFile, boolean removeCover) {
        if (removeCover || coverFile == null || coverFile.isEmpty()) {
            return BookCoverStorageService.PLACEHOLDER;
        }
        return bookCoverStorageService.store(coverFile);
    }

    private String resolveUpdatedCover(String existingCover, MultipartFile coverFile, boolean removeCover) {
        if (removeCover) {
            return BookCoverStorageService.PLACEHOLDER;
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            return bookCoverStorageService.store(coverFile);
        }
        if (existingCover == null || existingCover.isBlank()) {
            return BookCoverStorageService.PLACEHOLDER;
        }
        return existingCover;
    }

    // ---------- Categories ----------

    @GetMapping("/categories")
    public String categories(Model model) {
        Map<String, Long> bookCounts = new HashMap<>();
        categoryService.findAll().forEach(c ->
                bookCounts.put(c.getId(), (long) bookService.findByCategory(c.getId()).size()));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("bookCounts", bookCounts);
        model.addAttribute("pageTitle", "Manage Categories");
        model.addAttribute("adminSection", "categories");
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.create(name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Category created.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable String id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.update(id, name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ---------- Users ----------

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("users", userService.search(q));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Manage Users");
        model.addAttribute("adminSection", "users");
        return "admin/users";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable String id,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateByAdmin(id, fullName, email, UserRole.valueOf(role));
            redirectAttributes.addFlashAttribute("successMessage", "User updated.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteAccount(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ---------- Reviews ----------

    @GetMapping("/reviews")
    public String reviews(Model model) {
        Map<String, String> userNames = new HashMap<>();
        Map<String, String> bookTitles = new HashMap<>();
        reviewService.findAll().forEach(r -> {
            try {
                userNames.put(r.getUserId(), userService.getById(r.getUserId()).getFullName());
            } catch (Exception e) {
                userNames.put(r.getUserId(), "Unknown");
            }
            try {
                bookTitles.put(r.getBookId(), bookService.getById(r.getBookId()).getTitle());
            } catch (Exception e) {
                bookTitles.put(r.getBookId(), "Unknown book");
            }
        });
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("userNames", userNames);
        model.addAttribute("bookTitles", bookTitles);
        model.addAttribute("pageTitle", "Manage Reviews");
        model.addAttribute("adminSection", "reviews");
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            var review = reviewService.getById(id);
            reviewService.delete(id, review.getUserId(), true);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/reviews";
    }
}
