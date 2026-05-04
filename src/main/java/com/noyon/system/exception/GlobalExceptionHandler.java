package com.noyon.system.exception;

import com.noyon.system.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * REPLACED: exception/GlobalExceptionHandler.java
 *
 * WHY REPLACED:
 *   1. The old handler returned a raw Map<String, Object> — inconsistent with
 *      every other endpoint that returns ApiResponse.
 *   2. It had no handler for AccessDeniedException (403), so ownership
 *      violations silently fell through to the generic 500 handler.
 *   3. Validation errors had a different shape from other errors.
 *
 * NOW: All error responses use ApiResponse.error(message) — same envelope
 * as success responses. The frontend can always parse the same structure.
 *
 * Validation errors embed a `data` map of field → message pairs so the
 * frontend can highlight specific fields without parsing error strings.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 ─────────────────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ── 409 ─────────────────────────────────────────────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ── 401 ─────────────────────────────────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("E-posta veya şifre hatalı."));
    }

    // ── 403 ─────────────────────────────────────────────────────────────────
    // Thrown when a user tries to access another user's resource.
    @ExceptionHandler({AuthorizationDeniedException.class, org.springframework.security.access.AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleForbidden(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Bu kaynağa erişim yetkiniz yok."));
    }

    // ── 400 Validation ───────────────────────────────────────────────────────
    // Returns field-level errors so the frontend can highlight specific inputs.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field   = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });

        ApiResponse<Map<String, String>> body =
                ApiResponse.ok("Doğrulama hatası.", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    // ── 400 Illegal argument ─────────────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ── 500 Catch-all ────────────────────────────────────────────────────────
    // Never exposes internal error details to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        e.printStackTrace(); // EKLENEN HAYAT KURTARICI SATIR!
        return ResponseEntity.status(500).body(ApiResponse.error("Beklenmeyen bir hata oluştu."));
    }
    }