package com.familybooks.repository.file;

import com.familybooks.model.User;
import com.familybooks.model.UserRole;
import com.familybooks.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Text-file persistence for users.
 * Format: id|username|email|passwordHash|fullName|role|createdAt
 */
@Repository
public class FileUserRepository implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(FileUserRepository.class);
    private static final String FILE_NAME = "users.txt";

    private final FileStorageSupport storage;

    public FileUserRepository(FileStorageSupport storage) {
        this.storage = storage;
        this.storage.ensureFile(FILE_NAME);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (String line : storage.readLines(FILE_NAME)) {
            parseLine(line).ifPresent(users::add);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        return findAll().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String normalised = email.trim().toLowerCase();
        return findAll().stream().filter(u -> u.getEmail().equalsIgnoreCase(normalised)).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase(username.trim())).findFirst();
    }

    @Override
    public User save(User user) {
        List<User> users = findAll();
        boolean replaced = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            users.add(user);
        }
        persist(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        List<User> users = findAll();
        users.removeIf(u -> u.getId().equals(id));
        persist(users);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    private void persist(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(String.join(FileStorageSupport.FIELD_SEPARATOR,
                    storage.escape(user.getId()),
                    storage.escape(user.getUsername()),
                    storage.escape(user.getEmail()),
                    storage.escape(user.getPasswordHash()),
                    storage.escape(user.getFullName()),
                    user.getRole().name(),
                    user.getCreatedAt().toString()
            ));
        }
        storage.writeLines(FILE_NAME, lines);
    }

    private Optional<User> parseLine(String line) {
        try {
            String[] p = storage.splitFields(line, 7);
            User user = new User(
                    storage.unescape(p[0]),
                    storage.unescape(p[1]),
                    storage.unescape(p[2]),
                    storage.unescape(p[3]),
                    storage.unescape(p[4]),
                    UserRole.valueOf(p[5].trim()),
                    LocalDateTime.parse(p[6].trim())
            );
            return Optional.of(user);
        } catch (Exception e) {
            log.warn("Skipping corrupted user record: {}", line);
            return Optional.empty();
        }
    }
}
