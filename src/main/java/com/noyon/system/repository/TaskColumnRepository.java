package com.noyon.system.repository;

import com.noyon.system.entity.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {

    // Mevcut — dokunma
    List<TaskColumn> findByUserIdOrderByPositionAsc(Long userId);
    Optional<TaskColumn> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT MAX(c.position) FROM TaskColumn c WHERE c.user.id = :userId")
    Integer findMaxPositionByUserId(@Param("userId") Long userId);

    // YENİ — workspace desteği için ekle
    List<TaskColumn> findByUserIdAndWorkspaceIdOrderByPositionAsc(Long userId, Long workspaceId);

    @Query("SELECT MAX(c.position) FROM TaskColumn c WHERE c.user.id = :userId AND c.workspaceId = :workspaceId")
    Integer findMaxPositionByUserIdAndWorkspaceId(@Param("userId") Long userId, @Param("workspaceId") Long workspaceId);
}