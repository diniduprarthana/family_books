package com.familybooks.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract base class for all books in the Family Books catalogue.
 * Demonstrates abstraction, encapsulation, and polymorphic behaviour
 * via getBookType() and getAvailabilityDescription().
 */
public abstract class Book {

    private String id;
    private String title;
    private String author;
    private String isbn;
    private String categoryId;
    private String description;
    private BigDecimal price;
    private String coverImage;

    protected Book() {
    }

    protected Book(String id, String title, String author, String isbn,
                   String categoryId, String description, BigDecimal price, String coverImage) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        setCategoryId(categoryId);
        setDescription(description);
        setPrice(price);
        setCoverImage(coverImage);
    }

    /** Polymorphic: each concrete type returns its own type label. */
    public abstract String getBookType();

    /** Polymorphic: each concrete type describes availability differently. */
    public abstract String getAvailabilityDescription();

    /** Polymorphic: used when serialising type-specific fields to the text file. */
    public abstract String getTypeSpecificData();

    public abstract boolean isAvailable();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Book ID is required.");
        }
        this.id = id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book title is required.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author is required.");
        }
        this.author = author.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN is required.");
        }
        this.isbn = isbn.trim();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            throw new IllegalArgumentException("Category ID is required.");
        }
        this.categoryId = categoryId.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be zero or greater.");
        }
        this.price = price;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = (coverImage == null || coverImage.isBlank())
                ? "/images/covers/placeholder.svg"
                : coverImage.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book book)) {
            return false;
        }
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getBookType() + "{id='" + id + "', title='" + title + "'}";
    }
}
