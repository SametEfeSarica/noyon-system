package com.noyon.system.controller;

import com.noyon.system.entity.ChecklistItem;
import com.noyon.system.entity.ProjectTask;
import com.noyon.system.entity.TaskComment;
import com.noyon.system.service.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @PostMapping("/add")
    public ProjectTask addTask(@RequestBody ProjectTask task) {
        return projectTaskService.addTask(task);
    }

    // Swagger'daki /api/tasks/active/{userId} ucunu karşılıyor
    @GetMapping({"/active/{userId}", "/kanban/{userId}"})
    public List<ProjectTask> getActiveTasks(@PathVariable Long userId) {
        return projectTaskService.getTasksByUserId(userId);
    }

    @PutMapping("/update-status/{taskId}")
    public ProjectTask updateStatus(@PathVariable Long taskId, @RequestParam String status) {
        return projectTaskService.updateTaskStatus(taskId, status);
    }

    @DeleteMapping("/delete/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        projectTaskService.deleteTask(taskId);
    }

    // --- YENİ EKLENEN DETAY UÇLARI ---

    @PostMapping("/{taskId}/checklist")
    public ChecklistItem addChecklist(@PathVariable Long taskId, @RequestBody ChecklistItem item) {
        return projectTaskService.addChecklistItem(taskId, item);
    }

    @PutMapping("/checklist/{itemId}/toggle")
    public ChecklistItem toggleChecklist(@PathVariable Long itemId) {
        return projectTaskService.toggleChecklist(itemId);
    }

    @PostMapping("/{taskId}/comments")
    public TaskComment addComment(@PathVariable Long taskId, @RequestParam Long userId, @RequestParam String text) {
        return projectTaskService.addComment(taskId, userId, text);
    }
}