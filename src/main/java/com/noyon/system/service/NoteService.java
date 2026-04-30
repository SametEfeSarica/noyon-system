package com.noyon.system.service;

import com.noyon.system.entity.Note;
import com.noyon.system.repository.NoteRepository;
import com.noyon.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    // Aktif notları getir
    public List<Note> getActiveNotesByUserId(Long userId) {
        return noteRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    // Çöp kutusunu getir
    public List<Note> getTrashedNotesByUserId(Long userId) {
        return noteRepository.findByUser_IdAndIsDeletedTrue(userId);
    }

    // Not oluşturma (UserId ile birlikte)
    public Note saveNote(Note note, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        note.setUser(user);
        return noteRepository.save(note);
    }

    // Not Güncelleme (Renk, Başlık, İçerik vb.)
    public Note updateNote(Long noteId, Note noteDetails) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));

        note.setTitle(noteDetails.getTitle());
        note.setContent(noteDetails.getContent());
        note.setColor(noteDetails.getColor());
        note.setFolderName(noteDetails.getFolderName());

        return noteRepository.save(note);
    }

    // SOFT DELETE
    public void deleteNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));
        note.setDeleted(true);
        noteRepository.save(note);
    }

    // RESTORE
    public void restoreNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));
        note.setDeleted(false);
        noteRepository.save(note);
    }
}