package com.noyon.system.controller;

import com.noyon.system.entity.Note;
import com.noyon.system.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    // Yeni not ekleme
    @PostMapping("/add")
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        Note savedNote = noteService.createNote(note);
        return ResponseEntity.ok(savedNote);
    }

    // Belirli bir kullanıcının tüm notlarını getir
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getUserNotes(@PathVariable Long userId) {
        List<Note> notes = noteService.getNotesByUserId(userId);
        return ResponseEntity.ok(notes);
    }

    // Not silme
    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<String> deleteNote(@PathVariable Long noteId) {
        String result = noteService.deleteNote(noteId);
        return ResponseEntity.ok(result);
    }
}