package com.noyon.system.service;

import com.noyon.system.dto.note.CreateNoteRequest;
import com.noyon.system.dto.note.NoteResponse;
import com.noyon.system.dto.note.UpdateNoteRequest;
import com.noyon.system.entity.Folder;
import com.noyon.system.entity.Note;
import com.noyon.system.entity.User;
import com.noyon.system.exception.ResourceNotFoundException;
import com.noyon.system.mapper.NoteMapper;
import com.noyon.system.repository.FolderRepository;
import com.noyon.system.repository.NoteRepository;
import com.noyon.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * REPLACED: service/NoteService.java
 *
 * PROBLEMS FIXED:
 *
 * 1. RuntimeException everywhere
 *    Old: throw new RuntimeException("Not bulunamadı")
 *    New: throw new ResourceNotFoundException("Note", noteId)
 *    This maps to HTTP 404 via GlobalExceptionHandler instead of HTTP 500.
 *
 * 2. No ownership verification
 *    Old: noteRepository.findById(noteId) — any user could update/delete any note.
 *    New: noteRepository.findByIdAndUserId(noteId, userId) — returns empty if the
 *    note doesn't belong to this user, then throws AccessDeniedException → HTTP 403.
 *
 * 3. Entity returned from service methods
 *    Old: public Note saveNote(...) — service returned entity, controller returned entity.
 *    New: public NoteResponse create(...) — service returns DTO, controller returns DTO.
 *    The entity never leaves the service layer.
 *
 * 4. @Transactional missing on write operations
 *    Old: noteRepository.save(note) was called without a transaction boundary.
 *    If Hibernate had to flush multiple times (e.g. save + relationship update),
 *    a failure midway left partial data.
 *    New: All write methods are @Transactional.
 *
 * 5. Folder field update was wrong
 *    Old: note.setFolder(noteDetails.getFolder()) — set the folder from the incoming
 *    Note entity, which meant the frontend had to send a full Folder object in the JSON.
 *    New: accepts a folderId Long. The service resolves the Folder entity itself.
 *
 * 6. No @Transactional(readOnly = true) on read methods
 *    Hibernate optimises read-only transactions by skipping dirty checking.
 *    Added on all getX() methods.
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository  noteRepository;
    private final UserRepository  userRepository;
    private final FolderRepository folderRepository;
    private final NoteMapper      noteMapper;

    // ── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NoteResponse> getActiveNotes(Long userId) {
        return noteRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getTrashedNotes(Long userId) {
        return noteRepository.findByUserIdAndDeletedTrue(userId)
                .stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> searchNotes(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getActiveNotes(userId);
        }
        return noteRepository.searchByKeyword(userId, keyword.trim())
                .stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    @Transactional
    public NoteResponse create(Long userId, CreateNoteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .color(request.getColor())
                .imageUrl(request.getImageUrl())
                .pdfUrl(request.getPdfUrl())
                .handwritingBase64(request.getHandwritingBase64())
                .user(user)
                .build();

        if (request.getFolderId() != null) {
            Folder folder = folderRepository.findById(request.getFolderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Folder", request.getFolderId()));
            note.setFolder(folder);
        }

        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse update(Long userId, Long noteId, UpdateNoteRequest request) {
        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setColor(request.getColor());
        note.setImageUrl(request.getImageUrl());
        note.setPdfUrl(request.getPdfUrl());
        note.setHandwritingBase64(request.getHandwritingBase64());
        note.setPinned(request.isPinned());
        note.setFavorite(request.isFavorite());

        if (request.getFolderId() != null) {
            Folder folder = folderRepository.findById(request.getFolderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Folder", request.getFolderId()));
            note.setFolder(folder);
        } else {
            note.setFolder(null);
        }

        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Transactional
    public void softDelete(Long userId, Long noteId) {
        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));
        note.setDeleted(true);
        noteRepository.save(note);
    }

    @Transactional
    public NoteResponse restore(Long userId, Long noteId) {
        // Use findById here — a deleted note won't show in findByIdAndIsDeletedFalse,
        // but we still own it. Ownership is verified by userId check below.
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        if (!note.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bu nota erişim yetkiniz yok.");
        }

        note.setDeleted(false);
        return noteMapper.toResponse(noteRepository.save(note));
    }
}