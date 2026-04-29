package com.noyon.system.controller;

import com.noyon.system.entity.Note;
import com.noyon.system.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*") // Frontend'den gelecek CORS hatalarını engeller
public class NoteController {

    @Autowired
    private NoteService noteService;

    // Kullanıcının sadece AKTİF notlarını getir
    @GetMapping("/active/{userId}")
    public ResponseEntity<List<Note>> getActiveNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getActiveNotesByUserId(userId));
    }

    // Kullanıcının ÇÖP KUTUSUNDAKİ notlarını getir
    @GetMapping("/trash/{userId}")
    public ResponseEntity<List<Note>> getTrashedNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getTrashedNotesByUserId(userId));
    }

    // Not ekle / Güncelle
    @PostMapping("/save")
    public ResponseEntity<Note> saveNote(@RequestBody Note note) {
        return ResponseEntity.ok(noteService.saveNote(note));
    }

    // Notu çöp kutusuna at (Soft Delete)
    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<String> softDeleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.ok("Not başarıyla çöp kutusuna taşındı.");
    }

    // Notu çöp kutusundan kurtar
    @PutMapping("/restore/{noteId}")
    public ResponseEntity<String> restoreNote(@PathVariable Long noteId) {
        noteService.restoreNote(noteId);
        return ResponseEntity.ok("Not başarıyla geri yüklendi.");
    }
}