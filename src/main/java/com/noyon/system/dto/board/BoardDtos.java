package com.noyon.system.dto.board;

import lombok.*;
import java.util.List;

public class BoardDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardResponse {
        private Long id;
        private String title;
        private String description;
        private String priority;
        private String dueDate;
        private Integer position;
        private Long columnId;
        private List<AssigneeDto> assignees;
        private List<String> labels;
        private List<ChecklistItemDto> checklist;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssigneeDto {
        private Long id;
        private String name;
        private String initials;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChecklistItemDto {
        private Long id;
        private String text;
        private boolean done;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnResponse {
        private Long id;
        private String title;
        private String color;
        private Integer position;
        private List<CardResponse> cards;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardResponse {
        private List<ColumnResponse> columns;
        private List<AssigneeDto> members;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateColumnRequest {
        private String title;
        private String color;
        private Long workspaceId;  // ← bunu ekle
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateColumnRequest {
        private String title;
        private String color;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCardRequest {
        private String title;
        private String description;
        private String priority;
        private String dueDate;
        private Integer position; // DÜZELTME: MySQL çökmesini önlemek için eklendi
        private List<Long> assigneeIds;
        private List<String> labels;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCardRequest {
        private String title;
        private String description;
        private String priority;
        private String dueDate;
        private Integer position;
        private Long columnId;
        private List<Long> assigneeIds;
        private List<String> labels;
        private List<ChecklistItemDto> checklist;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoveCardRequest {
        private Long targetColumnId;
        private Integer newPosition;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReorderColumnsRequest {
        private List<Long> columnIds;
    }

}

