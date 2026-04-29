package com.noyon.system.repository;

import com.noyon.system.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    // GÜNCELLEME: isDeleted alanı tabloda olmadığı için hata veriyordu.
    // Sadece kullanıcı ID'sine göre görevleri sayacak hale getirdik.
    long countByUserId(Long userId);

    // Kullanıcıya göre tüm görevleri getirir
    List<ProjectTask> findByUserId(Long userId);
}