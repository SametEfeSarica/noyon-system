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
        // 1. Kullanıcı varlık kontrolü
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // 2. Notlar: Aktif olanları sayar (isDeleted=false)
        long notesCount = noteRepository.countByUser_IdAndIsDeletedFalse(userId);

        // 3. Kitaplar: isDeleted alanı olmadığı için sadece kullanıcı ID ile sayıyoruz
        long booksCount = libraryItemRepository.countByUser_Id(userId);

        // 4. Görevler: Loglarda is_deleted sütunu eklendiği için bu şekilde devam ediyoruz
        long tasksCount = projectTaskRepository.countByUser_IdAndIsDeletedFalse(userId);

        // 5. Abonelik ücreti toplamı
        Double totalCost = subscriptionRepository.getTotalSubscriptionCostByUserId(userId);
        if (totalCost == null) totalCost = 0.0;

        // 6. Özet DTO verisi döndürülür
        return new DashboardSummaryDTO(
                user.getUsername(),
                notesCount,
                booksCount,
                tasksCount,
                totalCost
        );
    }
}