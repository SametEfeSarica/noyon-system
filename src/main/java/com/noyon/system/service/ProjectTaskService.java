package com.noyon.system.service;

import com.noyon.system.entity.ProjectTask;
import com.noyon.system.entity.TaskColumn;
import com.noyon.system.entity.User;
import com.noyon.system.repository.ProjectTaskRepository;
import com.noyon.system.repository.TaskColumnRepository;
import com.noyon.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;
    private final TaskColumnRepository taskColumnRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectTask addTask(Long userId, Long columnId, ProjectTask task) {
        // Kullanıcı kontrolü
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı! ID: " + userId));

        // Sütun kontrolü
        TaskColumn column = taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Sütun bulunamadı! ID: " + columnId));

        // İlişkileri kur (Entity'deki nullable=false hatasını önlemek için şart)
        task.setUser(user);
        task.setColumn(column);

        // Veritabanına kayıt
        return projectTaskRepository.save(task);
    }
}