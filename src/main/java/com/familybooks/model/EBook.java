package com.familybooks.model;

import java.math.BigDecimal;

/**
 * Concrete book type representing a digital edition.
 * Availability is based on digital format / access, not physical stock.
 */
public class EBook extends Book {

    private String digitalFormat;
    private String downloadAccess;

    public EBook() {
        super();
    }

    public EBook(String id, String title, String author, String isbn,
                 String categoryId, String description, BigDecimal price,
                 String coverImage, String digitalFormat, String downloadAccess) {
        super(id, title, author, isbn, categoryId, description, price, coverImage);
        setDigitalFormat(digitalFormat);
        setDownloadAccess(downloadAccess);
    }

    @Override
    public String getBookType() {
        return "EBOOK";
    }

    @Override
    public String getAvailabilityDescription() {
        return "Instant digital download (" + digitalFormat + ")";
    }

    @Override
    public String getTypeSpecificData() {
        return digitalFormat + ";" + downloadAccess;
    }

    @Override
    public boolean isAvailable() {
        return digitalFormat != null && !digitalFormat.isBlank();
    }

    public String getDigitalFormat() {
        return digitalFormat;
    }

    public void setDigitalFormat(String digitalFormat) {
        if (digitalFormat == null || digitalFormat.isBlank()) {
            throw new IllegalArgumentException("Digital format is required for e-books.");
        }
        this.digitalFormat = digitalFormat.trim().toUpperCase();
    }

    public String getDownloadAccess() {
        return downloadAccess;
    }

    public void setDownloadAccess(String downloadAccess) {
        this.downloadAccess = downloadAccess != null ? downloadAccess.trim() : "Immediate";
    }
}
