package com.noyon.system.repository;

import com.noyon.system.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // DÜZELTME: isDeleted -> deleted yapıldı
    List<Note> findByUserIdAndDeletedFalse(Long userId);

    List<Note> findByUserIdAndDeletedTrue(Long userId);

    long countByUserIdAndDeletedFalse(Long userId);

    Optional<Note> findByIdAndUserId(Long noteId, Long userId);

    // DÜZELTME: Query içinde n.isDeleted yerine n.deleted kullanıldı
    @Query("""
        SELECT n FROM Note n
        WHERE n.user.id = :userId
          AND n.deleted = false
          AND (LOWER(n.title)   LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<Note> searchByKeyword(@Param("userId") Long userId,
                               @Param("keyword") String keyword);
}