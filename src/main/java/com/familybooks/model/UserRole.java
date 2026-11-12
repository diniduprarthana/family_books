package com.familybooks.model;

/**
 * Application roles used by Spring Security authorization.
 * Ordinary users cannot self-assign ADMIN.
 */
public enum UserRole {
    CUSTOMER,
    ADMIN
}
