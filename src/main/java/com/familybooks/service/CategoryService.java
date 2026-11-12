package com.familybooks.service;

import com.familybooks.exception.DuplicateResourceException;
import com.familybooks.exception.InvalidOperationException;
import com.familybooks.exception.ResourceNotFoundException;
import com.familybooks.model.Category;
import com.familybooks.repository.BookRepository;
import com.familybooks.repository.CategoryRepository;
import com.familybooks.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public Category create(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        categoryRepository.findByNameIgnoreCase(name).ifPresent(c -> {
            throw new DuplicateResourceException("Category already exists: " + name);
        });
        Category category = new Category(IdGenerator.next("CAT"), name.trim(), description);
        return categoryRepository.save(category);
    }

    public Category createWithIdIfAbsent(String id, String name, String description) {
        return categoryRepository.findById(id).orElseGet(() ->
                categoryRepository.save(new Category(id, name, description)));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(Category::getName))
                .toList();
    }

    public Category getById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    public Category update(String id, String name, String description) {
        Category category = getById(id);
        categoryRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Another category already uses this name.");
            }
        });
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    public void delete(String id) {
        Category category = getById(id);
        long bookCount = bookRepository.findByCategoryId(id).size();
        if (bookCount > 0) {
            throw new InvalidOperationException(
                    "Cannot delete category \"" + category.getName()
                            + "\" because " + bookCount + " book(s) still reference it. "
                            + "Reassign or delete those books first.");
        }
        categoryRepository.deleteById(id);
    }

    public long count() {
        return categoryRepository.count();
    }
}
