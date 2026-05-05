package com.noyon.system.mapper;

import com.noyon.system.dto.note.NoteResponse;
import com.noyon.system.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note note) {
        if (note == null) {
            return null;
        }

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