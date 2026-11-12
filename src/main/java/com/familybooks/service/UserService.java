package com.familybooks.service;

import com.familybooks.exception.DuplicateResourceException;
import com.familybooks.exception.InvalidOperationException;
import com.familybooks.exception.ResourceNotFoundException;
import com.familybooks.model.User;
import com.familybooks.model.UserRole;
import com.familybooks.repository.ReviewRepository;
import com.familybooks.repository.UserRepository;
import com.familybooks.util.IdGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ReviewRepository reviewRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String email, String rawPassword, String fullName) {
        validateRegistration(username, email, rawPassword, fullName);
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("This username is already taken.");
        }

        User user = new User(
                IdGenerator.next("USR"),
                username.trim(),
                email.trim().toLowerCase(Locale.ROOT),
                passwordEncoder.encode(rawPassword),
                fullName.trim(),
                UserRole.CUSTOMER,
                LocalDateTime.now()
        );
        return userRepository.save(user);
    }

    public User createAdminIfAbsent(String username, String email, String rawPassword, String fullName) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User admin = new User(
                IdGenerator.next("USR"),
                username,
                email.toLowerCase(Locale.ROOT),
                passwordEncoder.encode(rawPassword),
                fullName,
                UserRole.ADMIN,
                LocalDateTime.now()
        );
        return userRepository.save(admin);
    }

    public List<User> findAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .toList();
    }

    public List<User> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.toLowerCase(Locale.ROOT);
        return findAll().stream()
                .filter(u -> u.getUsername().toLowerCase(Locale.ROOT).contains(q)
                        || u.getEmail().toLowerCase(Locale.ROOT).contains(q)
                        || u.getFullName().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateProfile(String id, String fullName, String email) {
        User user = getById(id);
        if (email != null && !email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }
        user.setFullName(fullName);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public User updateByAdmin(String id, String fullName, String email, UserRole role) {
        User user = getById(id);
        if (email != null && !email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }
        user.setFullName(fullName);
        user.setEmail(email);
        if (role != null) {
            user.setRole(role);
        }
        return userRepository.save(user);
    }

    public void changePassword(String id, String currentPassword, String newPassword) {
        User user = getById(id);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidOperationException("Current password is incorrect.");
        }
        validatePassword(newPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteAccount(String id) {
        User user = getById(id);
        reviewRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }

    public long count() {
        return userRepository.findAll().size();
    }

    private void validateRegistration(String username, String email, String password, String fullName) {
        if (username == null || username.isBlank() || username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email is required.");
        }
        validatePassword(password);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
    }
}
