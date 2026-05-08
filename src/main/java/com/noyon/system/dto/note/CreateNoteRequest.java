package com.noyon.system.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CreateNoteRequest {

    @NotBlank(message = "Not başlığı boş olamaz.")
    @Size(max = 255, message = "Başlık en fazla 255 karakter olabilir.")
    private String title;

    private String content;

    @Size(max = 20)
    private String color;

    private String noteType; // ← EKLENDİ: "text" | "draw" | "pdf"

    private Long folderId;
    private String imageUrl;
    private String pdfUrl;
    private String handwritingBase64;
    private String pdfAnnotations;
    private List<String> tags;
}