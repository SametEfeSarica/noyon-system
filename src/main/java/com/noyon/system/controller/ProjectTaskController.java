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
@CrossOrigin(origins = "*")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService taskService;

    @PostMapping("/add")
    public ResponseEntity<ProjectTask> addTask(@Valid @RequestBody ProjectTask task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProjectTask>> getUserTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }

    @PutMapping("/update-status/{taskId}")
    public ResponseEntity<ProjectTask> updateStatus(@PathVariable Long taskId, @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }
}