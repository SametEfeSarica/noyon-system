package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    // GÜNCELLEME: isDeleted alanı tabloda olmadığı için onu sildik.
    // Sadece kullanıcıya ait kitapları sayar.
    long countByUser_Id(Long userId);

    // Kullanıcıya göre tüm öğeleri getirir
    List<LibraryItem> findByUser_Id(Long userId);

    // Başlığa göre arama yapar
    List<LibraryItem> findByTitleContainingIgnoreCase(String title);

    // Duruma göre filtreleme yapar
    List<LibraryItem> findByStatus(String status);
}