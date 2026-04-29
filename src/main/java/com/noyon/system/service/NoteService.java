package com.noyon.system.service;

import com.noyon.system.entity.Note;
import com.noyon.system.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    // Aktif notları getir
    public List<Note> getActiveNotesByUserId(Long userId) {
        return noteRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    // Çöp kutusunu getir
    public List<Note> getTrashedNotesByUserId(Long userId) {
        return noteRepository.findByUser_IdAndIsDeletedTrue(userId);
    }

    // Yeni not oluşturma / Kaydetme
    public Note saveNote(Note note) {
        return noteRepository.save(note);
    }

    // --- FİZİKSEL DEĞİL, MANTIKSAL SİLME (SOFT DELETE) ---
    public void deleteNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı: " + noteId));

        note.setDeleted(true); // Veritabanında kalır ama silinmiş işaretlenir
        noteRepository.save(note);
    }

    // Çöp kutusundan geri alma (Restore) - Bonus olarak ekledim :)
    public void restoreNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı: " + noteId));

        note.setDeleted(false);
        noteRepository.save(note);
    }
}