package com.noyon.system.exception;

/**
 * KEPT & CLEANED: exception/ResourceNotFoundException.java
 *
 * WHY: Previously every service threw RuntimeException("Not bulunamadı") with
 * a hardcoded Turkish string embedded in business logic. Two problems:
 *   1. RuntimeException is caught by every catch(Exception e) block — callers
 *      cannot distinguish "not found" from a real crash.
 *   2. The GlobalExceptionHandler had no specific handler for it so everything
 *      became HTTP 500 instead of HTTP 404.
 *
 * This typed exception lets GlobalExceptionHandler map it to exactly 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " bulunamadı. ID: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}