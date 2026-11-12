package com.familybooks.exception;

/**
 * Thrown when creating a resource that already exists (e.g. duplicate email).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
