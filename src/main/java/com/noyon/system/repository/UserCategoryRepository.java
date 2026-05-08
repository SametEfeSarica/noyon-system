// repository/UserCategoryRepository.java
package com.noyon.system.repository;

import com.noyon.system.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    List<UserCategory> findByUserId(Long userId);
    void deleteByUserIdAndCategoryId(Long userId, String categoryId);
    boolean existsByUserIdAndCategoryId(Long userId, String categoryId);
}