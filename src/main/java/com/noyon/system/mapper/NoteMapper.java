package com.noyon.system.mapper;

import com.noyon.system.dto.note.NoteResponse;
import com.noyon.system.entity.Note;
import org.springframework.stereotype.Component;

/**
 * CREATED NEW: mapper/NoteMapper.java
 *
 * WHY:
 * Previously the conversion from Note entity to the response shape happened
 * inline in the controller with ObjectMapper or by returning the entity directly.
 * Problems:
 *   1. Entity returned directly leaks internal structure.
 *   2. Conversion logic scattered — when the entity changes, every controller
 *      that returns it must be updated.
 *   3. Folder null-check and name extraction was never done — if a note had no
 *      folder, accessing note.getFolder().getName() caused NPE.
 *
 * The mapper centralises the Entity → DTO conversion in one place.
 * All null checks for optional fields (folder, imageUrl, etc.) live here.
 *
 * Note: We use a manual mapper rather than MapStruct here because:
 *   1. The note entity has a nested `folder` that needs null-safe name extraction.
 *   2. MapStruct would need additional configuration for the LAZY folder load.
 *   3. The codebase is small enough that manual mapping is readable and maintainable.
 *   MapStruct can be introduced later when the entity count grows.
 */
@Component
public class NoteMapper {

    /**
     * Converts a Note entity to NoteResponse.
     *
     * IMPORTANT: This must be called within an active transaction (or with
     * the folder already initialised) because `note.getFolder()` may trigger
     * a LAZY load. NoteService ensures this by running inside @Transactional.
     */
    public NoteResponse toResponse(Note note) {
        if (note == null) return null;

        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .color(note.getColor())
                .deleted(note.isDeleted())
                .pinned(note.isPinned())
                .favorite(note.isFavorite())
                .imageUrl(note.getImageUrl())
                .pdfUrl(note.getPdfUrl())
                .handwritingBase64(note.getHandwritingBase64())
                .folderId(note.getFolder() != null ? note.getFolder().getId() : null)
                .folderName(note.getFolder() != null ? note.getFolder().getName() : null)
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}