package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    // Eskiden tüm kitapları sayan metod
    long countByUser_Id(Long userId);

    // İŞTE HATAYI ÇÖZEN SATIR: Sadece çöpe atılmamış (aktif) kitapları sayan metod
    long countByUser_IdAndIsDeletedFalse(Long userId);

    List<LibraryItem> findByUser_Id(Long userId);

    // --- SOFT DELETE LİSTELEME ---
    List<LibraryItem> findByUser_IdAndIsDeletedFalse(Long userId);
    List<LibraryItem> findByUser_IdAndIsDeletedTrue(Long userId);

    List<LibraryItem> findByTitleContainingIgnoreCase(String title);

    List<LibraryItem> findByStatus(String status);
}