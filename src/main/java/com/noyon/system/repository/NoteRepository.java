package com.noyon.system.repository;

import com.noyon.system.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Service katmanındaki getActiveNotesByUserId için:
    List<Note> findByUserIdAndIsDeletedFalse(Long userId);

    // Service katmanındaki getTrashedNotesByUserId için:
    List<Note> findByUserIdAndIsDeletedTrue(Long userId);

    // DashboardService'deki sayım için:
    long countByUserIdAndIsDeletedFalse(Long userId);

    // GÖREV: Arama Metodu (Başlık veya İçerik)
    List<Note> findByUserIdAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(Long userId, String title, String content);
}