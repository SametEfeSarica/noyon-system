// ─── CreateNoteRequest.java ───────────────────────────────────────────────────
// FILE: dto/note/CreateNoteRequest.java
// CREATED NEW
//
// WHY: The old NoteController accepted Map<String, Object> and used ObjectMapper
// to manually convert it to a Note entity. Problems:
//   1. No validation — a null title was silently saved.
//   2. Entity used directly as input means any field on Note can be set by the client,
//      including `user`, `isDeleted`, `id` — a security and integrity risk.
//   3. ObjectMapper.convertValue(map, Note.class) fails silently on type mismatches.
//
// This DTO accepts only the fields the client is allowed to send.
// @Valid + @NotBlank enforces title is present before the service is even called.

package com.noyon.system.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateNoteRequest {

    @NotBlank(message = "Not başlığı boş olamaz.")
    @Size(max = 255, message = "Başlık en fazla 255 karakter olabilir.")
    private String title;

    private String content;

    @Size(max = 20)
    private String color;

    private Long folderId;

    private String imageUrl;

    private String pdfUrl;

    private String handwritingBase64;
}