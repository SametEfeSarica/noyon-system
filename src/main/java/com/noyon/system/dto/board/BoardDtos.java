package com.noyon.system.dto.board;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO katmanı — Entity'ler JSON'a direkt serialize edilmez.
 *
 * Neden ayrı DTO?
 * - User entity'si (şifre hash'i, roller) sızmaz.
 * - @JsonIgnore ile döngü kırma hackleri gerekmez.
 * - Frontend'in beklediği shape tam olarak kontrol edilir.
 * - API versiyonlama kolaylaşır.
 */
public class BoardDtos {

    // ── Kart yanıt DTO'su ─────────────────────────────────────────────────────

    @Data
    @Builder
    public static class CardResponse {
        private Long id;
        private String title;
        private String description;
        private String priority;      // "urgent" | "high" | "medium" | "low"
        private String dueDate;       // ISO yyyy-MM-dd string (frontend formatDate kullanıyor)
        private Integer position;
        private Long columnId;        // Frontend drag & drop için gerekli
        private List<AssigneeDto> assignees;
        private List<String> labels;
        private List<ChecklistItemDto> checklist;
    }

    @Data
    @Builder
    public static class AssigneeDto {
        private Long id;
        private String name;
        private String initials;      // "EY" gibi — Avatar component kullanıyor
    }

    @Data
    @Builder
    public static class ChecklistItemDto {
        private Long id;
        private String text;
        private boolean done;         // Frontend: item.done
    }

    // ── Sütun yanıt DTO'su ───────────────────────────────────────────────────

    @Data
    @Builder
    public static class ColumnResponse {
        private Long id;
        private String title;
        private String color;         // Hex "#6c6af6"
        private Integer position;
        private List<CardResponse> cards;
    }

    // ── Pano yanıt DTO'su (tek endpoint'ten tümünü al) ───────────────────────

    @Data
    @Builder
    public static class BoardResponse {
        private List<ColumnResponse> columns;
        private List<AssigneeDto> members;  // Projedeki tüm kullanıcılar (Avatar listesi)
    }

    // ── İstek DTO'ları ────────────────────────────────────────────────────────

    @Data
    public static class CreateColumnRequest {
        private String title;
        private String color;
    }

    @Data
    public static class UpdateColumnRequest {
        private String title;
        private String color;
    }

    @Data
    public static class CreateCardRequest {
        private String title;
        private String description;
        private String priority;
        private String dueDate;       // "yyyy-MM-dd" veya null
        private List<Long> assigneeIds;
        private List<String> labels;
    }

    @Data
    public static class UpdateCardRequest {
        private String title;
        private String description;
        private String priority;
        private String dueDate;
        private Long columnId;        // Kart farklı sütuna taşınabilir (modal'dan)
        private List<Long> assigneeIds;
        private List<String> labels;
        private List<ChecklistItemDto> checklist;
    }

    /** Drag & drop: kart columnId ve/veya position değişimi */
    @Data
    public static class MoveCardRequest {
        private Long targetColumnId;
        private Integer newPosition;  // Hedef sütundaki yeni index
    }

    /** Sütun sırası değişimi */
    @Data
    public static class ReorderColumnsRequest {
        private List<Long> columnIds; // Yeni sırayla tüm column id'leri
    }
}
