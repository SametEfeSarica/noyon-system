package com.noyon.system.service;

import com.noyon.system.dto.DashboardSummaryDTO;
import com.noyon.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final SubscriptionRepository subscriptionRepository;

    public DashboardSummaryDTO getDashboardSummary(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // Repository'deki yeni isimlere uyarlandı
        long notesCount = noteRepository.countByUserIdAndDeletedFalse(userId);

        // Repository'deki yeni isimlere uyarlandı
        long booksCount = libraryItemRepository.countByUser_IdAndDeletedFalse(userId);

        long tasksCount = projectTaskRepository.countByUserId(userId);

        Double totalCost = subscriptionRepository.getTotalSubscriptionCostByUserId(userId);
        if (totalCost == null) totalCost = 0.0;

        return DashboardSummaryDTO.builder()
                .username(user.getUsername())
                .notesCount(notesCount)
                .booksCount(booksCount)
                .tasksCount(tasksCount)
                .totalMonthlyCost(totalCost)
                .upcomingPayments(new java.util.ArrayList<>()) // Şimdilik boş liste
                .build();
    }
}