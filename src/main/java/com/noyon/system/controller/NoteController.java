package com.noyon.system.controller;

import com.noyon.system.entity.Note;
import com.noyon.system.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*") // PM'in talimatı: CORS İzni
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // Aktif notları getir
    @GetMapping("/active/{userId}")
    public ResponseEntity<List<Note>> getActiveNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getActiveNotesByUserId(userId));
    }

    // Çöp kutusunu getir
    @GetMapping("/trash/{userId}")
    public ResponseEntity<List<Note>> getTrashedNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getTrashedNotesByUserId(userId));
    }

    // GÖREV: Gelişmiş Arama (Keyword ile başlık veya içerikte arar)
    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam Long userId, @RequestParam String keyword) {
        return ResponseEntity.ok(noteService.searchNotes(userId, keyword));
    }

    // GÖREV: Yeni not ekle (Resim, PDF ve Base64 desteği Note entity içinden gelir)
    @PostMapping("/add/{userId}")
    public ResponseEntity<Note> addNote(@PathVariable Long userId, @RequestBody Note note) {
        return ResponseEntity.ok(noteService.saveNote(note, userId));
    }

    @PutMapping("/update/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable Long noteId, @RequestBody Note note) {
        return ResponseEntity.ok(noteService.updateNote(noteId, note));
    }

    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<String> softDeleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.ok("Not çöp kutusuna taşındı.");
    }

    @PutMapping("/restore/{noteId}")
    public ResponseEntity<String> restoreNote(@PathVariable Long noteId) {
        noteService.restoreNote(noteId);
        return ResponseEntity.ok("Not geri yüklendi.");
    }
}