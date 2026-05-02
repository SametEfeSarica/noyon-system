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

        // Notlar: Yeni Repository metoduna göre güncellendi
        long notesCount = noteRepository.countByUserIdAndIsDeletedFalse(userId);

        // Kitaplar: Mevcut yapıyı korur
        long booksCount = libraryItemRepository.countByUser_IdAndIsDeletedFalse(userId);

        long tasksCount = projectTaskRepository.countByUserId(userId);

        Double totalCost = subscriptionRepository.getTotalSubscriptionCostByUserId(userId);
        if (totalCost == null) totalCost = 0.0;

        return DashboardSummaryDTO.builder()
                .username(user.getUsername())
                .notesCount(notesCount)
                .booksCount(booksCount)
                .tasksCount(tasksCount)
                .totalMonthlyCost(totalCost != null ? totalCost : 0.0)
                .upcomingPayments(new java.util.ArrayList<>()) // Şimdilik boş bir liste gönderiyoruz
                .build();
    }
}