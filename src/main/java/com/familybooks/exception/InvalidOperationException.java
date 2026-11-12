package com.familybooks.exception;

/**
 * Thrown when a business rule prevents an operation (e.g. deleting a category in use).
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
