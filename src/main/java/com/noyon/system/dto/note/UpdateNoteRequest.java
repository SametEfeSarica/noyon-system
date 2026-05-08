package com.noyon.system.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class UpdateNoteRequest {

    @NotBlank(message = "Not başlığı boş olamaz.")
    @Size(max = 255)         // ← bu title'a ait, noteType'a değil
    private String title;

    private String content;

    @Size(max = 20)
    private String color;

    private String noteType; // ← doğru yere taşındı

    private Long folderId;
    private String imageUrl;
    private String pdfUrl;
    private String handwritingBase64;
    private String pdfAnnotations;
    private boolean pinned;
    private boolean favorite;
    private List<String> tags;
}