package com.familybooks.service;

import com.familybooks.exception.ResourceNotFoundException;
import com.familybooks.model.Book;
import com.familybooks.model.EBook;
import com.familybooks.model.PrintedBook;
import com.familybooks.repository.BookRepository;
import com.familybooks.repository.CategoryRepository;
import com.familybooks.repository.ReviewRepository;
import com.familybooks.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository,
                       ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
    }

    public Book createPrinted(String title, String author, String isbn, String categoryId,
                              String description, BigDecimal price, String coverImage,
                              int stockQuantity, int pageCount) {
        validateCategory(categoryId);
        PrintedBook book = new PrintedBook(
                IdGenerator.next("BK"), title, author, isbn, categoryId,
                description, price, coverImage, stockQuantity, pageCount);
        return bookRepository.save(book);
    }

    public Book createEBook(String title, String author, String isbn, String categoryId,
                            String description, BigDecimal price, String coverImage,
                            String digitalFormat, String downloadAccess) {
        validateCategory(categoryId);
        EBook book = new EBook(
                IdGenerator.next("BK"), title, author, isbn, categoryId,
                description, price, coverImage, digitalFormat, downloadAccess);
        return bookRepository.save(book);
    }

    public Book saveSeed(Book book) {
        if (bookRepository.existsById(book.getId())) {
            return bookRepository.findById(book.getId()).orElse(book);
        }
        return bookRepository.save(book);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findRecent(int limit) {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getId).reversed())
                .limit(limit)
                .toList();
    }

    public List<Book> findLowStock(int threshold) {
        return bookRepository.findAll().stream()
                .filter(b -> b instanceof PrintedBook printed && printed.getStockQuantity() <= threshold)
                .sorted(Comparator.comparing(b -> ((PrintedBook) b).getStockQuantity()))
                .toList();
    }

    public Book getById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    public List<Book> findByCategory(String categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    public List<Book> searchFilterSort(String query, String categoryId, String sort) {
        List<Book> books = bookRepository.search(query);
        if (categoryId != null && !categoryId.isBlank()) {
            books = books.stream().filter(b -> b.getCategoryId().equals(categoryId)).toList();
        }
        return sortBooks(books, sort);
    }

    public Book update(String id, String title, String author, String isbn, String categoryId,
                       String description, BigDecimal price, String coverImage, String bookType,
                       Integer stockQuantity, Integer pageCount,
                       String digitalFormat, String downloadAccess) {
        Book existing = getById(id);
        validateCategory(categoryId);

        Book updated;
        if ("EBOOK".equalsIgnoreCase(bookType)) {
            updated = new EBook(id, title, author, isbn, categoryId, description, price, coverImage,
                    digitalFormat != null ? digitalFormat : "PDF",
                    downloadAccess != null ? downloadAccess : "Immediate");
        } else {
            int stock = stockQuantity != null ? stockQuantity
                    : (existing instanceof PrintedBook pb ? pb.getStockQuantity() : 0);
            int pages = pageCount != null ? pageCount
                    : (existing instanceof PrintedBook pb ? pb.getPageCount() : 1);
            updated = new PrintedBook(id, title, author, isbn, categoryId, description, price, coverImage, stock, pages);
        }
        return bookRepository.save(updated);
    }

    public void delete(String id) {
        getById(id);
        reviewRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    public long count() {
        return bookRepository.count();
    }

    /**
     * Demonstrates polymorphism: base-class references invoke overridden methods.
     */
    public List<String> describeAvailabilityForAll() {
        return bookRepository.findAll().stream()
                .map(book -> book.getTitle() + " [" + book.getBookType() + "] — " + book.getAvailabilityDescription())
                .toList();
    }

    private void validateCategory(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category does not exist: " + categoryId);
        }
    }

    private List<Book> sortBooks(List<Book> books, String sort) {
        if (sort == null || sort.isBlank()) {
            sort = "title";
        }
        return switch (sort.toLowerCase(Locale.ROOT)) {
            case "price_asc" -> books.stream().sorted(Comparator.comparing(Book::getPrice)).toList();
            case "price_desc" -> books.stream().sorted(Comparator.comparing(Book::getPrice).reversed()).toList();
            case "author" -> books.stream().sorted(Comparator.comparing(Book::getAuthor)).toList();
            default -> books.stream().sorted(Comparator.comparing(Book::getTitle)).toList();
        };
    }
}
