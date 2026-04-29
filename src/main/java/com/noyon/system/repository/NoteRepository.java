package com.noyon.system.repository;

import com.noyon.system.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Dashboard için aktif not sayısını getiren yeni metodun
    long countByUser_IdAndIsDeletedFalse(Long userId);

    // Aktif notları liste olarak getirir
    List<Note> findByUser_IdAndIsDeletedFalse(Long userId);

    // Çöp kutusundaki notları liste olarak getirir
    List<Note> findByUser_IdAndIsDeletedTrue(Long userId);
}