package com.noyon.system.controller;

import com.noyon.system.entity.ProjectTask;
import com.noyon.system.service.ProjectTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Geliştirme aşamasında tüm isteklere izin verir
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    // DÜZELTME 1: React'in attığı GET isteğini karşılar ve 404/500 çökmesini durdurur.
    // Eğer ileride tüm taskları getiren bir servise ihtiyacınız olursa içini doldurabilirsiniz.
    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping("/add/{userId}/{columnId}")
    public ResponseEntity<ProjectTask> addTask(
            @PathVariable Long userId,
            @PathVariable Long columnId,
            @RequestBody ProjectTask task) {

        ProjectTask savedTask = projectTaskService.addTask(userId, columnId, task);
        return ResponseEntity.ok(savedTask);
    }
}