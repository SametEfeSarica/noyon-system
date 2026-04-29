package com.noyon.system.controller;

import com.noyon.system.dto.DashboardSummaryDTO;
import com.noyon.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard") // Ana adresimiz bu: localhost:8080/api/dashboard
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // KRİTİK: Emrah'ın tarayıcısı "Dur yolcu!" demesin diye izni verdik
public class DashboardController {

    private final DashboardService dashboardService;

    // Emrah'ın çağıracağı adres tam olarak bu: GET /api/dashboard/summary/1
    @GetMapping("/summary/{userId}")
    public DashboardSummaryDTO getSummary(@PathVariable Long userId) {
        // Senin yazdığın o muazzam servisi burada çağırıyoruz
        return dashboardService.getDashboardSummary(userId);
    }
}