package com.noyon.system.service;

import com.noyon.system.dto.workspace.WorkspaceDtos.*;
import com.noyon.system.entity.User;
import com.noyon.system.entity.Workspace;
import com.noyon.system.exception.ResourceNotFoundException;
import com.noyon.system.repository.TaskColumnRepository;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final TaskColumnRepository columnRepository;

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getWorkspaces(Long userId) {
        List<Workspace> workspaces = workspaceRepository.findByUserIdOrderByPositionAsc(userId);

        // Kullanıcının hiç workspace'i yoksa otomatik "Genel" oluştur
        if (workspaces.isEmpty()) {
            workspaces = List.of(ensureDefaultWorkspace(userId));
        }

        return workspaces.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest req, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        Integer maxPos = workspaceRepository.findMaxPositionByUserId(userId);
        int nextPos = (maxPos != null) ? maxPos + 1 : 0;

        Workspace ws = Workspace.builder()
                .name(req.getName() != null && !req.getName().isBlank() ? req.getName().trim() : "Yeni Alan")
                .color(req.getColor() != null ? req.getColor() : "#6c6af6")
                .position(nextPos)
                .user(user)
                .build();

        return toResponse(workspaceRepository.save(ws));
    }

    @Transactional
    public WorkspaceResponse updateWorkspace(Long wsId, UpdateWorkspaceRequest req, Long userId) {
        Workspace ws = workspaceRepository.findByIdAndUserId(wsId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Çalışma alanı bulunamadı", wsId));

        if (req.getName() != null && !req.getName().isBlank()) ws.setName(req.getName().trim());
        if (req.getColor() != null) ws.setColor(req.getColor());

        return toResponse(workspaceRepository.save(ws));
    }

    @Transactional
    public void deleteWorkspace(Long wsId, Long userId) {
        // Kullanıcının en az 1 workspace'i kalmalı
        List<Workspace> all = workspaceRepository.findByUserIdOrderByPositionAsc(userId);
        if (all.size() <= 1) {
            throw new IllegalStateException("Son çalışma alanı silinemez.");
        }

        Workspace ws = workspaceRepository.findByIdAndUserId(wsId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Çalışma alanı bulunamadı", wsId));

        // Bu workspace'e ait sütunları da sil (cascade yoksa manuel)
        // TaskColumn'da workspaceId sadece Long olduğu için orphan removal çalışmaz.
        // En temiz çözüm: sütunları önce sil.
        columnRepository.findByUserIdAndWorkspaceIdOrderByPositionAsc(userId, wsId)
                .forEach(columnRepository::delete);

        workspaceRepository.delete(ws);
    }

    // ── İlk girişte "Genel" workspace otomatik oluştur ───────────────────────────
    private Workspace ensureDefaultWorkspace(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Workspace ws = Workspace.builder()
                .name("Genel")
                .color("#6c6af6")
                .position(0)
                .user(user)
                .build();
        return workspaceRepository.save(ws);
    }

    private WorkspaceResponse toResponse(Workspace ws) {
        return WorkspaceResponse.builder()
                .id(ws.getId())
                .name(ws.getName())
                .color(ws.getColor())
                .position(ws.getPosition())
                .build();
    }
}
