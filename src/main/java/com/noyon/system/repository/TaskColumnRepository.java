package com.noyon.system.repository;

import com.noyon.system.entity.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {

    /** Kullanıcının sütunlarını position sırasıyla getir */
    List<TaskColumn> findByUserIdOrderByPositionAsc(Long userId);

    /** Mevcut max position — yeni sütun eklenirken kullanılır */
    @Query("SELECT MAX(c.position) FROM TaskColumn c WHERE c.user.id = :userId")
    Integer findMaxPositionByUserId(@Param("userId") Long userId);

    Optional<TaskColumn> findByIdAndUserId(Long id, Long userId);
}