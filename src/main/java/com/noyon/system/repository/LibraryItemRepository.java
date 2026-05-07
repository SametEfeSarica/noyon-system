package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    // 1. Dashboard "Kütüphane Sayısı" için özel sorgu
    // NULL olanları ve false olanları dahil eder, user_id eşleşmesine bakar.
    @Query("SELECT COUNT(l) FROM LibraryItem l WHERE l.user.id = :userId AND (l.deleted = false OR l.deleted IS NULL)")
    long countActiveBooksByUserId(@Param("userId") Long userId);

    // 2. Dashboard "Son Eklenen Kitaplar" listesi için özel sorgu
    // NULL olanları ve false olanları dahil eder, sıralamayı Pageable halleder.
    @Query("SELECT l FROM LibraryItem l WHERE l.user.id = :userId AND (l.deleted = false OR l.deleted IS NULL)")
    List<LibraryItem> findRecentActiveBooksByUserId(@Param("userId") Long userId, Pageable pageable);

    // 3. Kütüphane ana sayfası için (Tüm aktif kitaplar)
    @Query("SELECT l FROM LibraryItem l WHERE l.user.id = :userId AND (l.deleted = false OR l.deleted IS NULL)")
    List<LibraryItem> findByUserIdAndDeletedFalse(@Param("userId") Long userId);

    // 4. Çöp kutusu için
    List<LibraryItem> findByUserIdAndDeletedTrue(Long userId);

    // 5. Arama için
    @Query("SELECT l FROM LibraryItem l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%')) AND (l.deleted = false OR l.deleted IS NULL)")
    List<LibraryItem> findByTitleContainingIgnoreCaseAndDeletedFalse(@Param("title") String title);
}