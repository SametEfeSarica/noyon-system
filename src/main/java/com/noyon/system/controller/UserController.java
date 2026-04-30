package com.noyon.system.controller;

import com.noyon.system.entity.User;
import com.noyon.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Frontend erişimi için hayati
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(convertToResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            User user = userService.loginUser(username, password);
            return ResponseEntity.ok(convertToResponse(user));
        } catch (Exception e) {
            // Giriş başarısızsa 401 Unauthorized dönüyoruz
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Güvenlik: Şifreyi gizleyip sadece gerekli verileri dönen metod
    private Map<String, Object> convertToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        return response;
    }
}