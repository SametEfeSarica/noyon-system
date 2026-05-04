package com.noyon.system.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * CREATED NEW: dto/note/UpdateNoteRequest.java
 *
 * WHY separate from CreateNoteRequest:
 * Update has an additional `deleted` field that Create should never accept
 * (a newly created note can never be deleted). Keeping them separate means
 * each DTO describes exactly what that operation accepts — no ambiguity.
 *
 * This also supports the autosave use case: the frontend sends the full note
 * state on every autosave trigger, including `deleted = false` to confirm
 * the note is still active.
 */
@Data
public class UpdateNoteRequest {

    @NotBlank(message = "Not başlığı boş olamaz.")
    @Size(max = 255)
    private String title;

    private String content;

    @Size(max = 20)
    private String color;

    private Long folderId;

    private String imageUrl;

    private String pdfUrl;

    private String handwritingBase64;

    private boolean pinned;

    private boolean favorite;
}