package com.familybooks.repository.file;

import com.familybooks.model.Category;
import com.familybooks.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Text-file persistence for categories.
 * Format: id|name|description
 */
@Repository
public class FileCategoryRepository implements CategoryRepository {

    private static final Logger log = LoggerFactory.getLogger(FileCategoryRepository.class);
    private static final String FILE_NAME = "categories.txt";

    private final FileStorageSupport storage;

    public FileCategoryRepository(FileStorageSupport storage) {
        this.storage = storage;
        this.storage.ensureFile(FILE_NAME);
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        for (String line : storage.readLines(FILE_NAME)) {
            parseLine(line).ifPresent(categories::add);
        }
        return categories;
    }

    @Override
    public Optional<Category> findById(String id) {
        return findAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Category> findByNameIgnoreCase(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    @Override
    public Category save(Category category) {
        List<Category> categories = findAll();
        boolean replaced = false;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(category.getId())) {
                categories.set(i, category);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            categories.add(category);
        }
        persist(categories);
        return category;
    }

    @Override
    public void deleteById(String id) {
        List<Category> categories = findAll();
        categories.removeIf(c -> c.getId().equals(id));
        persist(categories);
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        return findAll().size();
    }

    private void persist(List<Category> categories) {
        List<String> lines = new ArrayList<>();
        for (Category category : categories) {
            lines.add(String.join(FileStorageSupport.FIELD_SEPARATOR,
                    storage.escape(category.getId()),
                    storage.escape(category.getName()),
                    storage.escape(category.getDescription())
            ));
        }
        storage.writeLines(FILE_NAME, lines);
    }

    private Optional<Category> parseLine(String line) {
        try {
            String[] p = storage.splitFields(line, 3);
            return Optional.of(new Category(
                    storage.unescape(p[0]),
                    storage.unescape(p[1]),
                    storage.unescape(p[2])
            ));
        } catch (Exception e) {
            log.warn("Skipping corrupted category record: {}", line);
            return Optional.empty();
        }
    }
}
