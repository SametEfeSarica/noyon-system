package com.noyon.system.dto.note;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NoteResponse {
    private Long id;
    private String title;
    private String content;
    private String color;
    private boolean deleted;
    private boolean pinned;
    private boolean favorite;
    private String imageUrl;
    private String pdfUrl;
    private String handwritingBase64;
    private Long folderId;
    private String folderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
}