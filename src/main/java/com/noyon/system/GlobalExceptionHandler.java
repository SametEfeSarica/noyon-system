package com.noyon.system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Bu notasyon, bu sınıfın projedeki tüm hataları dinlemesini sağlar.
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // Herhangi bir hata olduğunda burası çalışır.
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage()); // Emrah'a giden temiz mesaj.
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}