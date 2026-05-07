package com.noyon.system.repository;

import com.noyon.system.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // Kullanıcının aktif (silinmemiş) notlarını getirir
    List<Note> findByUserIdAndDeletedFalse(Long userId);

    // Kullanıcının çöp kutusundaki notlarını getirir
    List<Note> findByUserIdAndDeletedTrue(Long userId);
    List<Note> findByUserIdAndDeletedFalse(Long userId, org.springframework.data.domain.Pageable pageable);
    // Güvenlik: Sadece o kullanıcıya ait olan bir notu bulur
    Optional<Note> findByIdAndUserId(Long id, Long userId);

    // Kullanıcının aktif (silinmemiş) notlarının toplam sayısını getirir (Dashboard için)
    long countByUserIdAndDeletedFalse(Long userId);

    // Arama motoru: Başlıkta veya içerikte kelime arar (BÜYÜK/küçük harf duyarsız)
    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.deleted = false " +
            "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}