package com.noyon.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardSummaryDTO {
    private String username;
    private long notesCount;
    private long booksCount;
    private long tasksCount;
    private double totalMonthlyCost;
    private List<UpcomingPayment> upcomingPayments;

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