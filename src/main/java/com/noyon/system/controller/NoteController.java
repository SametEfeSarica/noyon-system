package com.noyon.system.controller;

import com.noyon.system.entity.Note;
import com.noyon.system.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // DÜZELTME 1: "/active/{userId}" yerine frontend'in beklediği "/{userId}" yapıldı.
    @GetMapping("/{userId}")
    public ResponseEntity<List<Note>> getActiveNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getActiveNotesByUserId(userId));
    }

    @GetMapping("/trash/{userId}")
    public ResponseEntity<List<Note>> getTrashedNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getTrashedNotesByUserId(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam Long userId, @RequestParam String keyword) {
        return ResponseEntity.ok(noteService.searchNotes(userId, keyword));
    }

    // DÜZELTME 2: Frontend ID'yi URL'den değil JSON gövdesinden yolluyor. Parametreler Map'e çevrildi.
    @PostMapping("/add")
    public ResponseEntity<Note> addNote(@RequestBody Map<String, Object> payload) {
        ObjectMapper mapper = new ObjectMapper();
        Note note = mapper.convertValue(payload, Note.class);
        Long userId = Long.valueOf(payload.get("userId").toString());

        return ResponseEntity.ok(noteService.saveNote(note, userId));
    }

    @PutMapping("/update/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable Long noteId, @RequestBody Map<String, Object> payload) {
        ObjectMapper mapper = new ObjectMapper();
        Note note = mapper.convertValue(payload, Note.class);
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