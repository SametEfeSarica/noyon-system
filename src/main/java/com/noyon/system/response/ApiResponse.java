package com.noyon.system.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

/**
 * CREATED: response/ApiResponse.java
 *
 * WHY: Every controller previously returned a different shape:
 *   - Some returned raw entities
 *   - Some returned Map<String, Object>
 *   - Some returned ResponseEntity<String> with plain text
 *   - Some returned ResponseEntity<List<Entity>>
 *
 * This made the frontend impossible to handle consistently — every endpoint
 * needed its own parsing logic. This wrapper enforces a single contract:
 *
 *   { "success": true, "message": "...", "data": { ... } }
 *
 * The frontend can always check `response.data.success` and access `response.data.data`.
 * @JsonInclude(NON_NULL) means `data` is omitted from the JSON when null (e.g. DELETE responses),
 * keeping payloads minimal.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String  message;
    private final T       data;
    private final Instant timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.timestamp = Instant.now();
    }

    /** 200 / 201 with a body */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** 200 / 201 with no body (e.g. DELETE, PATCH that returns no payload) */
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /** Error factory — used internally by GlobalExceptionHandler */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}