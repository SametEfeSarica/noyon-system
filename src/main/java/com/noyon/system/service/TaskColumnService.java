package com.noyon.system.service;

import com.noyon.system.entity.TaskColumn;
import com.noyon.system.entity.User;
import com.noyon.system.repository.TaskColumnRepository;
import com.noyon.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskColumnService {

    private final TaskColumnRepository taskColumnRepository;
    private final UserRepository userRepository;

    public List<TaskColumn> getColumnsForUser(Long userId, Long workspaceId) {
        return taskColumnRepository.findByUserIdAndWorkspaceIdOrderByPositionAsc(userId, workspaceId);
    }

    @Transactional
    public TaskColumn createColumn(Long userId, String title, String color, Long workspaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));

        int nextPos = taskColumnRepository
                .findByUserIdAndWorkspaceIdOrderByPositionAsc(userId, workspaceId)
                .size();

        TaskColumn col = TaskColumn.builder()
                .title(title)
                .color(color != null ? color : "#6c6af6")
                .workspaceId(workspaceId)
                .position(nextPos)
                .user(user)
                .build();

        return taskColumnRepository.save(col);
    }

    @Transactional
    public TaskColumn updateColumn(Long columnId, Long userId, String title, String color) {
        TaskColumn col = taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Sütun bulunamadı: " + columnId));

        if (!col.getUser().getId().equals(userId))
            throw new RuntimeException("Yetkisiz işlem");

        if (title != null) col.setTitle(title);
        if (color != null) col.setColor(color);

        return taskColumnRepository.save(col);
    }

    @Transactional
    public void deleteColumn(Long columnId, Long userId) {
        TaskColumn col = taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Sütun bulunamadı: " + columnId));

        if (!col.getUser().getId().equals(userId))
            throw new RuntimeException("Yetkisiz işlem");

        taskColumnRepository.delete(col);
    }
}