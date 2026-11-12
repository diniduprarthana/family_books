package com.familybooks.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookPolymorphismTest {

    @Test
    void baseClassReference_invokesOverriddenMethods() {
        List<Book> catalogue = new ArrayList<>();
        catalogue.add(new PrintedBook("1", "Printed", "Author", "isbn1", "CAT", "d",
                new BigDecimal("10"), "/x.svg", 0, 100));
        catalogue.add(new EBook("2", "Digital", "Author", "isbn2", "CAT", "d",
                new BigDecimal("8"), "/y.svg", "PDF", "Immediate"));

        Book first = catalogue.get(0);
        Book second = catalogue.get(1);

        assertEquals("PRINTED", first.getBookType());
        assertEquals("Out of stock", first.getAvailabilityDescription());
        assertFalse(first.isAvailable());

        assertEquals("EBOOK", second.getBookType());
        assertTrue(second.getAvailabilityDescription().contains("PDF"));
        assertTrue(second.isAvailable());
    }
}
