package com.noyon.system.controller;

import com.noyon.system.dto.note.CreateNoteRequest;
import com.noyon.system.dto.note.NoteResponse;
import com.noyon.system.dto.note.UpdateNoteRequest;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*") // <-- EKLENEN KRİTİK SATIR: Frontend'in kapısını açar
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    private Long resolveUserId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow()
                .getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getActiveNotes(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        List<NoteResponse> notes = noteService.getActiveNotes(userId);
        return ResponseEntity.ok(ApiResponse.ok("Notlar getirildi.", notes));
    }

    @GetMapping("/trash")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getTrash(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        List<NoteResponse> notes = noteService.getTrashedNotes(userId);
        return ResponseEntity.ok(ApiResponse.ok("Çöp kutusu getirildi.", notes));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> search(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String keyword) {
        Long userId = resolveUserId(principal);
        List<NoteResponse> notes = noteService.searchNotes(userId, keyword);
        return ResponseEntity.ok(ApiResponse.ok("Arama tamamlandı.", notes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> create(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateNoteRequest request) {
        Long userId = resolveUserId(principal);
        NoteResponse created = noteService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Not oluşturuldu.", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> update(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request) {
        Long userId = resolveUserId(principal);
        NoteResponse updated = noteService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Not güncellendi.", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        Long userId = resolveUserId(principal);
        noteService.softDelete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Not çöp kutusuna taşındı."));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<NoteResponse>> restore(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        Long userId = resolveUserId(principal);
        NoteResponse restored = noteService.restore(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Not geri yüklendi.", restored));
    }
    // ── Çöp Kutusundan Kalıcı Olarak Silme ──
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<String>> deletePermanent(@PathVariable Long id) {
        noteService.permanentDelete(id);
        return ResponseEntity.ok(ApiResponse.ok("Not veritabanından kalıcı olarak silindi.", "OK"));
    }

}