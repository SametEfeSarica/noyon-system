package com.noyon.system.repository;

import com.noyon.system.entity.Note; // İŞTE GERÇEK ADRES BURASI!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Sadece giriş yapmış kullanıcının (kendi) notlarını getirmesi için
    List<Note> findByUserId(Long userId);
}