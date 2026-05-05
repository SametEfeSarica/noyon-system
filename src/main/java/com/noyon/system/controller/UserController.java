package com.noyon.system.controller;

import com.noyon.system.dto.UserDtos.*;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Profil bilgileri getirildi.", userService.getProfile(userId)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestAttribute("userId") Long userId,
            @RequestBody ProfileUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Profil güncellendi.", userService.updateProfile(userId, req)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestAttribute("userId") Long userId,
            @RequestBody PasswordChangeRequest req) {
        userService.changePassword(userId, req);
        return ResponseEntity.ok(ApiResponse.ok("Şifre değiştirildi.", null));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@RequestAttribute("userId") Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.ok(ApiResponse.ok("Hesap silindi.", null));
    }
}