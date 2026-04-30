package com.noyon.system.service;

import com.noyon.system.entity.ChecklistItem;
import com.noyon.system.entity.ProjectTask;
import com.noyon.system.entity.TaskComment;
import com.noyon.system.entity.User;
import com.noyon.system.repository.ChecklistItemRepository;
import com.noyon.system.repository.ProjectTaskRepository;
import com.noyon.system.repository.TaskCommentRepository;
import com.noyon.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private UserRepository userRepository;

    // Görev Ekleme
    public ProjectTask addTask(ProjectTask task) {
        // Eğer frontend'den user ID geldiyse, kullanıcıyı bulup göreve bağlıyoruz
        if (task.getUser() != null && task.getUser().getId() != null) {
            User user = userRepository.findById(task.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
            task.setUser(user);
        }
        return projectTaskRepository.save(task);
    }

    // Kullanıcıya ait görevleri getirme
    public List<ProjectTask> getTasksByUserId(Long userId) {
        return projectTaskRepository.findByUserId(userId);
    }

    // Görev durumunu güncelleme (Sürükle-Bırak için)
    public ProjectTask updateTaskStatus(Long taskId, String status) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        task.setStatus(status);
        return projectTaskRepository.save(task);
    }

    // Görevi Silme
    public void deleteTask(Long taskId) {
        projectTaskRepository.deleteById(taskId);
    }

    // --- YENİ EKLENEN DETAY METODLARI ---

    // Checklist Öğesi Ekle
    public ChecklistItem addChecklistItem(Long taskId, ChecklistItem item) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        item.setTask(task);
        return checklistItemRepository.save(item);
    }

    // Checklist Durumunu Değiştir (Tik atma/kaldırma)
    public ChecklistItem toggleChecklist(Long itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist öğesi bulunamadı!"));
        item.setCompleted(!item.isCompleted());
        return checklistItemRepository.save(item);
    }

    // Yorum Ekle
    public TaskComment addComment(Long taskId, Long userId, String text) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setText(text);
        comment.setTimeAgo("Az önce");
        return taskCommentRepository.save(comment);
    }
}