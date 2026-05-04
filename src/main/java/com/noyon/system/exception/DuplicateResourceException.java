package com.noyon.system.exception;

/**
 * KEPT: exception/DuplicateResourceException.java
 *
 * WHY: Used by AuthService when email/username is already taken.
 * Maps to HTTP 409 Conflict in GlobalExceptionHandler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}