package com.familybooks.repository;

import com.familybooks.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction for user persistence. Controllers never touch text files directly.
 */
public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User save(User user);

    void deleteById(String id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
