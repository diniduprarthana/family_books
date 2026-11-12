package com.familybooks.controller;

import com.familybooks.model.Book;
import com.familybooks.service.BookService;
import com.familybooks.service.CategoryService;
import com.familybooks.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private static final Map<String, String> CATEGORY_IMAGES = Map.of(
            "CAT-FICTION", "/images/categories/fiction.jpg",
            "CAT-NONFICTION", "/images/categories/nonfiction.jpg",
            "CAT-ROMANCE", "/images/categories/romance.jpg",
            "CAT-MYSTERY", "/images/categories/mystery.jpg",
            "CAT-BIOGRAPHY", "/images/categories/biography.jpg",
            "CAT-HISTORY", "/images/categories/history.jpg",
            "CAT-SCIENCE", "/images/categories/science.jpg",
            "CAT-CHILDREN", "/images/categories/children.jpg"
    );

    private final BookService bookService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public HomeController(BookService bookService,
                          CategoryService categoryService,
                          ReviewService reviewService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Book> featured = bookService.findRecent(6);
        Map<String, Double> ratings = new HashMap<>();
        for (Book book : featured) {
            ratings.put(book.getId(), reviewService.averageRating(book.getId()));
        }
        model.addAttribute("featuredBooks", featured);
        model.addAttribute("recentBooks", bookService.findRecent(4));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("categoryImages", CATEGORY_IMAGES);
        model.addAttribute("featuredRatings", ratings);
        model.addAttribute("pageTitle", "Home");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About");
        return "about";
    }
}
