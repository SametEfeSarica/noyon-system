package com.noyon.system.controller;

import com.noyon.system.dto.DashboardSummaryDTO;
import com.noyon.system.repository.UserRepository;
import com.noyon.system.response.ApiResponse;
import com.noyon.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getSummary(@AuthenticationPrincipal UserDetails principal) {
        Long userId = userRepository.findByEmail(principal.getUsername()).orElseThrow().getId();
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userId);
        return ResponseEntity.ok(ApiResponse.ok("Dashboard özeti getirildi.", summary));
    }
}