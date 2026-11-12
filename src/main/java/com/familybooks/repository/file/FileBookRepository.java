package com.familybooks.repository.file;

import com.familybooks.model.Book;
import com.familybooks.model.EBook;
import com.familybooks.model.PrintedBook;
import com.familybooks.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Text-file persistence for books with polymorphic reconstruction.
 * Format: id|title|author|isbn|categoryId|description|price|coverImage|bookType|typeSpecificData
 * Printed typeSpecificData: stock;pageCount
 * EBook typeSpecificData: format;downloadAccess
 */
@Repository
public class FileBookRepository implements BookRepository {

    private static final Logger log = LoggerFactory.getLogger(FileBookRepository.class);
    private static final String FILE_NAME = "books.txt";

    private final FileStorageSupport storage;

    public FileBookRepository(FileStorageSupport storage) {
        this.storage = storage;
        this.storage.ensureFile(FILE_NAME);
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        for (String line : storage.readLines(FILE_NAME)) {
            parseLine(line).ifPresent(books::add);
        }
        return books;
    }

    @Override
    public Optional<Book> findById(String id) {
        return findAll().stream().filter(b -> b.getId().equals(id)).findFirst();
    }

    @Override
    public List<Book> findByCategoryId(String categoryId) {
        return findAll().stream().filter(b -> b.getCategoryId().equals(categoryId)).toList();
    }

    @Override
    public List<Book> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.toLowerCase(Locale.ROOT);
        return findAll().stream()
                .filter(b -> b.getTitle().toLowerCase(Locale.ROOT).contains(q)
                        || b.getAuthor().toLowerCase(Locale.ROOT).contains(q)
                        || b.getIsbn().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    @Override
    public Book save(Book book) {
        List<Book> books = findAll();
        boolean replaced = false;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(book.getId())) {
                books.set(i, book);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            books.add(book);
        }
        persist(books);
        return book;
    }

    @Override
    public void deleteById(String id) {
        List<Book> books = findAll();
        books.removeIf(b -> b.getId().equals(id));
        persist(books);
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        return findAll().size();
    }

    private void persist(List<Book> books) {
        List<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(String.join(FileStorageSupport.FIELD_SEPARATOR,
                    storage.escape(book.getId()),
                    storage.escape(book.getTitle()),
                    storage.escape(book.getAuthor()),
                    storage.escape(book.getIsbn()),
                    storage.escape(book.getCategoryId()),
                    storage.escape(book.getDescription()),
                    book.getPrice().toPlainString(),
                    storage.escape(book.getCoverImage()),
                    book.getBookType(),
                    storage.escape(book.getTypeSpecificData())
            ));
        }
        storage.writeLines(FILE_NAME, lines);
    }

    private Optional<Book> parseLine(String line) {
        try {
            String[] p = storage.splitFields(line, 10);
            String id = storage.unescape(p[0]);
            String title = storage.unescape(p[1]);
            String author = storage.unescape(p[2]);
            String isbn = storage.unescape(p[3]);
            String categoryId = storage.unescape(p[4]);
            String description = storage.unescape(p[5]);
            BigDecimal price = new BigDecimal(p[6].trim());
            String cover = storage.unescape(p[7]);
            String type = p[8].trim().toUpperCase(Locale.ROOT);
            String specific = storage.unescape(p[9]);
            String[] extras = specific.split(";", -1);

            Book book;
            if ("EBOOK".equals(type)) {
                String format = extras.length > 0 ? extras[0] : "PDF";
                String access = extras.length > 1 ? extras[1] : "Immediate";
                book = new EBook(id, title, author, isbn, categoryId, description, price, cover, format, access);
            } else {
                int stock = extras.length > 0 && !extras[0].isBlank() ? Integer.parseInt(extras[0]) : 0;
                int pages = extras.length > 1 && !extras[1].isBlank() ? Integer.parseInt(extras[1]) : 1;
                book = new PrintedBook(id, title, author, isbn, categoryId, description, price, cover, stock, pages);
            }
            return Optional.of(book);
        } catch (Exception e) {
            log.warn("Skipping corrupted book record: {}", line);
            return Optional.empty();
        }
    }
}
