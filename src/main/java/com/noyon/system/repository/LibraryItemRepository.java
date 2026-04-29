package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    // DashboardService'deki hatayı çözen satır burasıdır.
    // İsim karakteri karakterine aynı olmalıdır: countByUser_Id
    long countByUser_Id(Long userId);

    // Diğer gerekli metodlar
    List<LibraryItem> findByUser_Id(Long userId);

    List<LibraryItem> findByTitleContainingIgnoreCase(String title);

    List<LibraryItem> findByStatus(String status);
}