package com.noyon.system.repository;

import com.noyon.system.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    // 1. Dashboard için: Aktif görev sayısını döndüren kritik metod (Kırmızı hatayı çözer)
    long countByUser_IdAndIsDeletedFalse(Long userId);

    // 2. Sadece AKTİF (çöpe atılmamış) tüm görevleri getir
    List<ProjectTask> findByUser_IdAndIsDeletedFalse(Long userId);

    // 3. KANBAN için: Sadece belirli bir statüdeki ve aktif görevleri getir
    List<ProjectTask> findByUser_IdAndStatusAndIsDeletedFalse(Long userId, String status);
}