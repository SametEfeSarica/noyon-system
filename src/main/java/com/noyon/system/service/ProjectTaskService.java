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

    // Yeni Görev Ekle / Kaydet
    public ProjectTask saveTask(ProjectTask task) {
        return taskRepository.save(task);
    }

    // Kullanıcının tüm aktif görevleri
    public List<ProjectTask> getActiveTasks(Long userId) {
        return taskRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    // Kanban panosu için statüye göre getirme
    public List<ProjectTask> getTasksByStatus(Long userId, String status) {
        return taskRepository.findByUser_IdAndStatusAndIsDeletedFalse(userId, status);
    }

    // Sürükle-Bırak statü güncellemesi
    public ProjectTask updateTaskStatus(Long taskId, String newStatus) {
        ProjectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    // --- FİZİKSEL DEĞİL, MANTIKSAL SİLME (SOFT DELETE) ---
    public void softDeleteTask(Long taskId) {
        ProjectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        task.setDeleted(true);
        taskRepository.save(task);
    }
}