package com.noyon.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardSummaryDTO {
    private String username;
    private String displayName;
    private long notesCount;
    private long booksCount;
    private long tasksCount;
    private double totalMonthlyCost;
    private List<UpcomingPayment> upcomingPayments;
    private List<RecentBookDTO> recentBooks;
    private List<RecentTaskDTO> recentTasks;
    private List<RecentNoteDTO> recentNotes;

    @Data
    @Builder
    public static class RecentNoteDTO {
        private Long id;
        private String title;
        private String content;
        private String color;
        private boolean pinned;
        private java.time.LocalDateTime updatedAt;
    }

    @Data
    @Builder
    public static class RecentBookDTO {
        private Long id;
        private String title;
        private String author;
    }

    @Data
    @Builder
    public static class RecentTaskDTO {
        private Long id;
        private String title;
        private String priority;
        private String status;
    }

    @Data
    @Builder
    public static class UpcomingPayment {
        private String platformName;
        private Double amount;
        private String currency;
        private Integer daysUntil;
        private Integer renewalDay;
    }
}