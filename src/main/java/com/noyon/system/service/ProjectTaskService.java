package com.noyon.system.service;

import com.noyon.system.entity.ProjectTask;
import com.noyon.system.repository.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository taskRepository;

    public ProjectTask createTask(ProjectTask task) {
        return taskRepository.save(task);
    }

    public List<ProjectTask> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public ProjectTask updateTaskStatus(Long taskId, String newStatus) {
        ProjectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public String deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
        return "Görev silindi!";
    }
}