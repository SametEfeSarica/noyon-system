package com.noyon.system.repository;

import com.noyon.system.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

    List<UserCategory> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserCategory c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}