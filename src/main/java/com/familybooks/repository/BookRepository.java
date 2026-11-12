package com.familybooks.repository;

import com.familybooks.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    List<Book> findAll();

    Optional<Book> findById(String id);

    List<Book> findByCategoryId(String categoryId);

    List<Book> search(String query);

    Book save(Book book);

    void deleteById(String id);

    boolean existsById(String id);

    long count();
}
