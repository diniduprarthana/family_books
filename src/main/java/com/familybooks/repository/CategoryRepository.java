package com.familybooks.repository;

import com.familybooks.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(String id);

    Optional<Category> findByNameIgnoreCase(String name);

    Category save(Category category);

    void deleteById(String id);

    boolean existsById(String id);

    long count();
}
