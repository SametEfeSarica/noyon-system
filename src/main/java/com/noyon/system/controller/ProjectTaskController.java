package com.noyon.system.controller;

import com.noyon.system.entity.ProjectTask;
import com.noyon.system.service.ProjectTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Geliştirme aşamasında tüm isteklere izin verir
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    // URL'den {userId} kısmını sildik, sadece {columnId} kaldı.
    @PostMapping("/add/{columnId}")
    public ResponseEntity<ProjectTask> addTask(
            @RequestAttribute("userId") Long userId, // Güvenli kaynağı (Filter'ı) kullanıyoruz
            @PathVariable Long columnId,
            @RequestBody ProjectTask task) {

        ProjectTask savedTask = projectTaskService.addTask(userId, columnId, task);
        return ResponseEntity.ok(savedTask);
    }
}