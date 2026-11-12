package com.familybooks.controller;

import com.familybooks.security.CustomUserDetails;
import com.familybooks.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public String create(@AuthenticationPrincipal CustomUserDetails currentUser,
                         @RequestParam String bookId,
                         @RequestParam int rating,
                         @RequestParam String reviewText,
                         RedirectAttributes redirectAttributes) {
        try {
            reviewService.create(bookId, currentUser.getUserId(), rating, reviewText);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your review.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/reviews/{id}/update")
    public String update(@PathVariable String id,
                         @AuthenticationPrincipal CustomUserDetails currentUser,
                         @RequestParam String bookId,
                         @RequestParam int rating,
                         @RequestParam String reviewText,
                         RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
            reviewService.update(id, currentUser.getUserId(), isAdmin, rating, reviewText);
            redirectAttributes.addFlashAttribute("successMessage", "Review updated.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/reviews/{id}/delete")
    public String delete(@PathVariable String id,
                         @AuthenticationPrincipal CustomUserDetails currentUser,
                         @RequestParam String bookId,
                         RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
            reviewService.delete(id, currentUser.getUserId(), isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/books/" + bookId;
    }
}
