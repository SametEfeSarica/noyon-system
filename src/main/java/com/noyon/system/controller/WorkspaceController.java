package com.noyon.system.controller;

import com.noyon.system.dto.workspace.WorkspaceDtos.*;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.WorkspaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
@Tag(name = "Çalışma Alanları")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserRepository userRepository;

    private Long userId(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"))
                .getId();
    }

    // GET /api/workspaces  → kullanıcının tüm workspace'leri
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getAll(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
                ApiResponse.ok("Çalışma alanları yüklendi.", workspaceService.getWorkspaces(userId(principal))));
    }

    // POST /api/workspaces  → yeni workspace
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceResponse>> create(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody CreateWorkspaceRequest request) {
        WorkspaceResponse ws = workspaceService.createWorkspace(request, userId(principal));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Çalışma alanı oluşturuldu.", ws));
    }

    // PATCH /api/workspaces/{id}  → güncelle
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> update(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody UpdateWorkspaceRequest request) {
        WorkspaceResponse ws = workspaceService.updateWorkspace(id, request, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Çalışma alanı güncellendi.", ws));
    }

    // DELETE /api/workspaces/{id}  → sil
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        workspaceService.deleteWorkspace(id, userId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Çalışma alanı silindi."));
    }
}
