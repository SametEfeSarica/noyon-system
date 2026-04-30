package com.noyon.system.controller;

import com.noyon.system.dto.DashboardSummaryDTO;
import com.noyon.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard") // Ana adres: localhost:8080/api/dashboard
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Emrah'ın frontend bağlantısı için CORS izni
public class DashboardController {

    private final DashboardService dashboardService;

    // Tam adres: GET /api/dashboard/summary/{userId}
    @GetMapping("/summary/{userId}")
    public DashboardSummaryDTO getSummary(@PathVariable Long userId) {
        // DashboardService içindeki getDashboardSummary metodunu tetikler
        return dashboardService.getDashboardSummary(userId);
    }
}