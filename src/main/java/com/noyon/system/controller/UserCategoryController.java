// controller/UserCategoryController.java
package com.noyon.system.controller;

import com.noyon.system.dto.UserCategoryDto;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.UserCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class UserCategoryController {

    private final UserCategoryService service;
    private final UserRepository userRepository;

    private Long resolveUserId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserCategoryDto>>> getAll(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok("Kategoriler getirildi.",
                service.getAll(resolveUserId(principal))));
    }

    // Tüm listeyi bir seferde kaydet (Category Manager'dan "Kaydet" butonuna basınca)
    @PostMapping("/save-all")
    public ResponseEntity<ApiResponse<List<UserCategoryDto>>> saveAll(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody List<UserCategoryDto> dtos) {
        return ResponseEntity.ok(ApiResponse.ok("Kategoriler kaydedildi.",
                service.saveAll(resolveUserId(principal), dtos)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Kategori silindi.", "OK"));
    }
}