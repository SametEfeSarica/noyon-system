package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    /** Kullanıcıya ait silinmemiş tüm kitaplar */
    List<LibraryItem> findByUserIdAndDeletedFalse(Long userId);

    /** Kategoriye göre filtrele (kullanıcıya ait, silinmemiş) */
    List<LibraryItem> findByUserIdAndCategoryAndDeletedFalse(Long userId, String category);

    /** Başlıkta arama (büyük/küçük harf duyarsız) */
    List<LibraryItem> findByTitleContainingIgnoreCaseAndDeletedFalse(String title);

    /** Çöp kutusundakiler */
    List<LibraryItem> findByUserIdAndDeletedTrue(Long userId);

    long countByUser_IdAndDeletedFalse(Long userId);
}
