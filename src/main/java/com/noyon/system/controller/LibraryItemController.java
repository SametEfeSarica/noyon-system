package com.noyon.system.controller;

import com.noyon.system.dto.LibraryItemDto;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.LibraryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Tag(name = "Kütüphane Yönetimi")
public class LibraryItemController {

    private final LibraryItemService libraryItemService;
    private final UserRepository     userRepository;

    private Long resolveUserId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow()
                .getId();
    }

    // ── GET /api/library ──────────────────────────────────────────────────────

    @Operation(summary = "Kullanıcıya ait tüm kitapları getirir")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LibraryItemDto>>> getAll(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        List<LibraryItemDto> items = libraryItemService.getAllByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("Kitaplar getirildi.", items));
    }

    // ── POST /api/library ─────────────────────────────────────────────────────

    @Operation(summary = "Yeni kitap ekler")
    @PostMapping
    public ResponseEntity<ApiResponse<LibraryItemDto>> create(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody LibraryItemDto.Request request) {
        Long userId = resolveUserId(principal);
        LibraryItemDto created = libraryItemService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Kitap başarıyla eklendi.", created));
    }

    // ── PUT /api/library/{id} ─────────────────────────────────────────────────

    @Operation(summary = "Kitabı günceller")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibraryItemDto>> update(
            @PathVariable Long id,
            @RequestBody LibraryItemDto.Request request) {
        LibraryItemDto updated = libraryItemService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Kitap güncellendi.", updated));
    }

    // ── PATCH /api/library/{id}/favorite ─────────────────────────────────────

    /**
     * Optimistic UI için tasarlandı.
     * Frontend anında kalbi doldurur, arka planda bu endpoint çağrılır.
     * Response: { "data": { "isFavorite": true } }
     */
    @Operation(summary = "Favori durumunu toggle eder")
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleFavorite(
            @PathVariable Long id) {
        boolean newState = libraryItemService.toggleFavorite(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Favori durumu güncellendi.",
                        Map.of("isFavorite", newState)));
    }

    // ── DELETE /api/library/{id} ──────────────────────────────────────────────

    @Operation(summary = "Kitabı çöpe atar (Soft Delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        libraryItemService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.ok("Kitap çöpe taşındı.", "OK"));
    }

    // ── GET /api/library/trash ────────────────────────────────────────────────

    @Operation(summary = "Çöp kutusundaki kitapları listeler")
    @GetMapping("/trash")
    public ResponseEntity<ApiResponse<List<LibraryItemDto>>> getTrash(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(
                ApiResponse.ok("Çöp kutusu getirildi.",
                        libraryItemService.getTrash(userId)));
    }

    // ── PATCH /api/library/{id}/restore ──────────────────────────────────────

    @Operation(summary = "Kitabı çöpten geri yükler")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<LibraryItemDto>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Kitap geri yüklendi.",
                        libraryItemService.restore(id)));
    }

    // ── GET /api/library/search ───────────────────────────────────────────────

    @Operation(summary = "Kitap ismine göre arama yapar")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LibraryItemDto>>> search(
            @RequestParam String title) {
        return ResponseEntity.ok(
                ApiResponse.ok("Arama sonuçları:",
                        libraryItemService.searchByTitle(title)));
    }
}
