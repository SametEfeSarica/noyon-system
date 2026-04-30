package com.noyon.system.service;

import com.noyon.system.entity.Note;
import com.noyon.system.repository.NoteRepository;
import com.noyon.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // @Autowired yerine daha güvenli olan constructor injection
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    // Aktif notları getir
    public List<Note> getActiveNotesByUserId(Long userId) {
        return noteRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    // Çöp kutusunu getir
    public List<Note> getTrashedNotesByUserId(Long userId) {
        return noteRepository.findByUserIdAndIsDeletedTrue(userId);
    }

    // GÖREV: Arama (Arama yaparken sadece silinmemiş notlarda ara)
    public List<Note> searchNotes(Long userId, String keyword) {
        return noteRepository.findByUserIdAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(userId, keyword, keyword);
    }

    // Not oluşturma (UserId ile birlikte)
    public Note saveNote(Note note, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        note.setUser(user);
        return noteRepository.save(note);
    }

    // GÖREV GÜNCELLEMESİ: Tüm medya ve klasör alanlarını destekler
    public Note updateNote(Long noteId, Note noteDetails) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Not bulunamadı"));

        note.setTitle(noteDetails.getTitle());
        note.setContent(noteDetails.getContent());
        note.setColor(noteDetails.getColor());

        // Yeni Alanlar
        note.setFolder(noteDetails.getFolder()); // String folderName yerine Folder entity
        note.setImageUrl(noteDetails.getImageUrl());
        note.setPdfUrl(noteDetails.getPdfUrl());
        note.setHandwritingBase64(noteDetails.getHandwritingBase64());

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