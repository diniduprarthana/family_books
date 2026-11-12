package com.familybooks.util;

import java.util.UUID;

/**
 * Generates stable unique IDs for domain entities.
 */
public final class IdGenerator {

    private IdGenerator() {
    }

    public static String next(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
