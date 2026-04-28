package com.noyon.system.repository;

import com.noyon.system.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    // KRİTİK DOKUNUŞ: Alt çizgi (_) ile Spring Boot'a "User objesinin içindeki id'ye bak" diyoruz.
    List<LibraryItem> findByUser_Id(Long userId);

    List<LibraryItem> findByTitleContainingIgnoreCase(String title);

    List<LibraryItem> findByStatus(String status);
}