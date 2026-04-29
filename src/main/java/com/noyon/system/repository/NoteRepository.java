package com.noyon.system.repository;

import com.noyon.system.entity.Note; // İŞTE GERÇEK ADRES BURASI!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Aktif notları getirir
    List<Note> findByUser_IdAndIsDeletedFalse(Long userId);

    // Çöp kutusundaki notları getirir
    List<Note> findByUser_IdAndIsDeletedTrue(Long userId);
}

