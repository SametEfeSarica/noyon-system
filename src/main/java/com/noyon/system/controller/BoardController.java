package com.noyon.system.controller;

import com.noyon.system.dto.board.BoardDtos.*;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "Kanban Panosu")
public class BoardController {

    private final BoardService boardService;
    private final UserRepository userRepository;

    private Long userId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"))
                .getId();
    }
    @GetMapping
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) Long workspaceId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Pano yüklendi.", boardService.getBoard(userId(principal), workspaceId)));
    }

    @PostMapping("/columns")
    public ResponseEntity<ApiResponse<ColumnResponse>> createColumn(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateColumnRequest request) {
        ColumnResponse col = boardService.createColumn(request, userId(principal));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Sütun oluşturuldu.", col));
    }

    @PatchMapping("/columns/{id}")
    public ResponseEntity<ApiResponse<ColumnResponse>> updateColumn(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody UpdateColumnRequest request) {
        ColumnResponse col = boardService.updateColumn(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Sütun güncellendi.", col));
    }

    @PostMapping("/columns/{columnId}/cards")
    public ResponseEntity<?> createCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long columnId,
            @Valid @RequestBody CreateCardRequest request) {
        try {
            // DÜZELTME 2: MySQL çökmesini (500 hatasını) engellemek için try-catch zırhı
            CardResponse card = boardService.createCard(columnId, request, userId(principal));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Kart oluşturuldu.", card));
        } catch (Exception e) {
            // Hata olursa sistemi çökertme, React'e okunabilir bir mesaj yolla
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Kart eklenemedi! Muhtemelen zorunlu bir veri (Örn: order, title) eksik gönderiliyor. Detay: " + e.getMessage());
        }
    }

    @PatchMapping("/cards/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardRequest request) {
        CardResponse card = boardService.updateCard(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Kart güncellendi.", card));
    }

    @PatchMapping("/cards/{id}/move")
    public ResponseEntity<ApiResponse<CardResponse>> moveCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody MoveCardRequest request) {
        CardResponse card = boardService.moveCard(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Kart taşındı.", card));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        boardService.deleteCard(id, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Kart silindi."));
    }

    @DeleteMapping("/columns/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteColumn(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        boardService.deleteColumn(id, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Sütun silindi."));
    }
}