package com.noyon.system.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

