package com.familybooks.model;

import java.math.BigDecimal;

/**
 * Concrete book type representing a physical printed edition.
 * Stock quantity controls physical availability.
 */
public class PrintedBook extends Book {

    private int stockQuantity;
    private int pageCount;

    public PrintedBook() {
        super();
    }

    public PrintedBook(String id, String title, String author, String isbn,
                       String categoryId, String description, BigDecimal price,
                       String coverImage, int stockQuantity, int pageCount) {
        super(id, title, author, isbn, categoryId, description, price, coverImage);
        setStockQuantity(stockQuantity);
        setPageCount(pageCount);
    }

    @Override
    public String getBookType() {
        return "PRINTED";
    }

    @Override
    public String getAvailabilityDescription() {
        if (stockQuantity <= 0) {
            return "Out of stock";
        }
        if (stockQuantity <= 5) {
            return "Only " + stockQuantity + " copies left";
        }
        return stockQuantity + " copies in stock";
    }

    @Override
    public String getTypeSpecificData() {
        return stockQuantity + ";" + pageCount;
    }

    @Override
    public boolean isAvailable() {
        return stockQuantity > 0;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        if (pageCount < 1) {
            throw new IllegalArgumentException("Page count must be at least 1.");
        }
        this.pageCount = pageCount;
    }
}
