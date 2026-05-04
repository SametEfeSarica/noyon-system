package com.noyon.system.repository;

import com.noyon.system.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByColumnIdOrderByPositionAsc(Long columnId);

    Optional<ProjectTask> findByIdAndUserId(Long id, Long userId);

    // EKSİK OLAN VE EKLENEN METOD BURASI: Dashboard'un toplam görev sayısını bulmasını sağlar
    long countByUserId(Long userId);

    /** Belirli sütundaki en büyük position — yeni kart sonuna eklenirken */
    @Query("SELECT COALESCE(MAX(t.position), -1) FROM ProjectTask t WHERE t.column.id = :columnId")
    int findMaxPositionByColumnId(Long columnId);

    /** Drag & drop sonrası position güncelleme (bulk) */
    @Modifying
    @Query("UPDATE ProjectTask t SET t.position = :position WHERE t.id = :id")
    void updatePosition(Long id, int position);

    /** Sütun değiştirme + position güncelleme */
    @Modifying
    @Query("UPDATE ProjectTask t SET t.column.id = :columnId, t.position = :position WHERE t.id = :id")
    void moveCard(Long id, Long columnId, int position);
}