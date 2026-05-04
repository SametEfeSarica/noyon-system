package com.noyon.system.dto.note;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CREATED NEW: dto/note/NoteResponse.java
 *
 * WHY: The old controller returned Note entity directly. The entity contains:
 *   - `user` field (even with @JsonIgnore, this creates a hidden coupling)
 *   - `folder` entity (triggers potential LAZY load if not inside a transaction)
 *   - No `folderName` string (frontend had to dereference the folder object)
 *   - No `createdAt` / `updatedAt` (didn't exist on the entity)
 *
 * This DTO projects exactly what the frontend needs:
 *   - `folderName` flattened from folder.getName()
 *   - `createdAt` / `updatedAt` for autosave confirmation UI
 *   - No user reference, no internal DB fields
 *
 * The `@Builder` annotation allows NoteMapper to construct instances fluently.
 */
@Data
@Builder
public class NoteResponse {

    private Long          id;
    private String        title;
    private String        content;
    private String        color;
    private boolean       deleted;
    private boolean       pinned;
    private boolean       favorite;
    private String        imageUrl;
    private String        pdfUrl;
    private String        handwritingBase64;
    private Long          folderId;
    private String        folderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}