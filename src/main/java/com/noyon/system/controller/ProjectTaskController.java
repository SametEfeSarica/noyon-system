package com.noyon.system.controller;

import com.noyon.system.entity.ProjectTask;
import com.noyon.system.service.ProjectTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Frontend'in tüm bağlantılarına izin verir
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService taskService;

    // Görev Ekle
    @PostMapping("/add")
    public ResponseEntity<ProjectTask> addTask(@Valid @RequestBody ProjectTask task) {
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    // Kullanıcının Tüm Aktif Görevleri
    @GetMapping("/active/{userId}")
    public ResponseEntity<List<ProjectTask>> getActiveTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getActiveTasks(userId));
    }

    // Kanban İçin: Belirli Statüdeki Görevler (Örn: /api/tasks/kanban/1?status=TODO)
    @GetMapping("/kanban/{userId}")
    public ResponseEntity<List<ProjectTask>> getTasksForKanban(@PathVariable Long userId, @RequestParam String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(userId, status));
    }

    // Sürükle Bırak (Statü Değiştir)
    @PutMapping("/update-status/{taskId}")
    public ResponseEntity<ProjectTask> updateStatus(@PathVariable Long taskId, @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    // Görevi Çöpe At (Soft Delete)
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> softDeleteTask(@PathVariable Long taskId) {
        taskService.softDeleteTask(taskId);
        return ResponseEntity.ok("Görev başarıyla çöp kutusuna taşındı.");
    }
}