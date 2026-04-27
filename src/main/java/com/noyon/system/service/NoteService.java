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

    // Yeni Not Oluştur
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    // Kullanıcının Tüm Notlarını Getir
    public List<Note> getNotesByUserId(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    // Not Sil
    public String deleteNote(Long noteId) {
        if (noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            return "Not başarıyla silindi.";
        }
        return "Hata: Not bulunamadı!";
    }
}