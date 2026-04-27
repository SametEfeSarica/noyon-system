package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
    // Kullanıcı ID'sine göre kitapları filtrelemek için
    List<LibraryItem> findByUserId(Long userId);
}