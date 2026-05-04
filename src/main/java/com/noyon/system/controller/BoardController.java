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

/**
 * BoardController — Kanban panosu REST API
 *
 * Endpoint özeti:
 *
 * GET    /api/board                              → Tüm panoyu tek seferde çek
 *
 * POST   /api/board/columns                      → Yeni sütun oluştur
 * PATCH  /api/board/columns/{id}                 → Sütunu güncelle (başlık/renk)
 * DELETE /api/board/columns/{id}                 → Sütunu sil (kartlarıyla birlikte)
 * PATCH  /api/board/columns/reorder              → Sütun sırasını kaydet
 *
 * POST   /api/board/columns/{colId}/cards        → Yeni kart oluştur
 * PATCH  /api/board/cards/{id}                   → Kartı güncelle (tam güncelleme)
 * PATCH  /api/board/cards/{id}/move              → Kart taşı (drag & drop)
 * DELETE /api/board/cards/{id}                   → Kartı sil
 * PATCH  /api/board/cards/{cardId}/checklist/{itemId}/toggle → Checklist toggle
 */
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "Kanban Panosu")
public class BoardController {

    private final BoardService    boardService;
    private final UserRepository  userRepository;

    // ── Kullanıcı kimliği çözücü ─────────────────────────────────────────────

    private Long userId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow()
                .getId();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANO
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Tüm panoyu getir (sütunlar + kartlar + üyeler)")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
                ApiResponse.ok("Pano yüklendi.", boardService.getBoard(userId(principal))));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SÜTUNLAR
    // ═══════════════════════════════════════════════════════════════════════════

    @PostMapping("/columns")
    @Operation(summary = "Yeni sütun oluştur")
    public ResponseEntity<ApiResponse<ColumnResponse>> createColumn(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateColumnRequest request) {
        ColumnResponse col = boardService.createColumn(request, userId(principal));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Sütun oluşturuldu.", col));
    }

    @PatchMapping("/columns/{id}")
    @Operation(summary = "Sütun başlığını veya rengini güncelle")
    public ResponseEntity<ApiResponse<ColumnResponse>> updateColumn(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody UpdateColumnRequest request) {
        ColumnResponse col = boardService.updateColumn(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Sütun güncellendi.", col));
    }

    @DeleteMapping("/columns/{id}")
    @Operation(summary = "Sütunu sil (içindeki kartlarla birlikte)")
    public ResponseEntity<ApiResponse<Void>> deleteColumn(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        boardService.deleteColumn(id, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Sütun silindi."));
    }

    @PatchMapping("/columns/reorder")
    @Operation(summary = "Sütunların sırasını kaydet (drag & drop sonrası)")
    public ResponseEntity<ApiResponse<Void>> reorderColumns(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody ReorderColumnsRequest request) {
        boardService.reorderColumns(request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Sütun sırası güncellendi."));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // KARTLAR
    // ═══════════════════════════════════════════════════════════════════════════

    @PostMapping("/columns/{columnId}/cards")
    @Operation(summary = "Sütuna yeni kart ekle")
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long columnId,
            @RequestBody CreateCardRequest request) {
        CardResponse card = boardService.createCard(columnId, request, userId(principal));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Kart oluşturuldu.", card));
    }

    @PatchMapping("/cards/{id}")
    @Operation(summary = "Kartı güncelle (başlık, açıklama, öncelik, tarih, atananlar, etiketler, checklist)")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody UpdateCardRequest request) {
        CardResponse card = boardService.updateCard(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Kart güncellendi.", card));
    }

    @PatchMapping("/cards/{id}/move")
    @Operation(summary = "Kartı taşı — drag & drop endpoint")
    public ResponseEntity<ApiResponse<CardResponse>> moveCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody MoveCardRequest request) {
        CardResponse card = boardService.moveCard(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Kart taşındı.", card));
    }

    @DeleteMapping("/cards/{id}")
    @Operation(summary = "Kartı sil")
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        boardService.deleteCard(id, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Kart silindi."));
    }

    @PatchMapping("/cards/{cardId}/checklist/{itemId}/toggle")
    @Operation(summary = "Checklist öğesini tamamlandı/tamamlanmadı olarak işaretle")
    public ResponseEntity<ApiResponse<CardResponse>> toggleChecklist(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long cardId,
            @PathVariable Long itemId) {
        CardResponse card = boardService.toggleChecklistItem(cardId, itemId, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Checklist güncellendi.", card));
    }
}
