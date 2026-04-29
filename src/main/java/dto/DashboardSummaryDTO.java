package com.noyon.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {
    private String userName;
    private long totalActiveNotes;
    private long totalActiveBooks;
    private long totalActiveTasks;
    private double totalSubscriptionCost;
}