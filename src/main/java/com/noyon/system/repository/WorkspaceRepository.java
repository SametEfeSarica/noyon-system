package com.noyon.system.repository;

import com.noyon.system.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    List<Workspace> findByUserIdOrderByPositionAsc(Long userId);

    Optional<Workspace> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT MAX(w.position) FROM Workspace w WHERE w.user.id = :userId")
    Integer findMaxPositionByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndName(Long userId, String name);
}
