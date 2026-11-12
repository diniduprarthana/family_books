package com.familybooks.controller;

import com.familybooks.model.Book;
import com.familybooks.model.Review;
import com.familybooks.security.CustomUserDetails;
import com.familybooks.service.BookService;
import com.familybooks.service.CategoryService;
import com.familybooks.service.ReviewService;
import com.familybooks.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final UserService userService;

    public BookController(BookService bookService,
                          CategoryService categoryService,
                          ReviewService reviewService,
                          UserService userService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/books")
    public String collection(@RequestParam(required = false) String q,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false, defaultValue = "title") String sort,
                             Model model) {
        List<Book> books = bookService.searchFilterSort(q, category, sort);
        model.addAttribute("books", books);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("pageTitle", "Collection");
        return "books/collection";
    }

    @GetMapping("/books/{id}")
    public String details(@PathVariable String id,
                          @AuthenticationPrincipal CustomUserDetails currentUser,
                          Model model) {
        Book book = bookService.getById(id);
        List<Review> reviews = reviewService.findByBookId(id);
        Map<String, String> reviewerNames = new HashMap<>();
        for (Review review : reviews) {
            try {
                reviewerNames.put(review.getUserId(), userService.getById(review.getUserId()).getFullName());
            } catch (Exception e) {
                reviewerNames.put(review.getUserId(), "Family Books Reader");
            }
        }

        model.addAttribute("book", book);
        model.addAttribute("category", categoryService.getById(book.getCategoryId()));
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewerNames", reviewerNames);
        model.addAttribute("averageRating", reviewService.averageRating(id));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pageTitle", book.getTitle());
        return "books/details";
    }
}
