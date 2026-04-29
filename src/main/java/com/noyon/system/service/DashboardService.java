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
        // 1. Kullanıcı kontrolü
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi!"));

        // 2. Notlar tablosunda isDeleted alanı var (Entity'de tanımlı), o yüzden False olanları sayıyoruz
        long notesCount = noteRepository.countByUser_IdAndIsDeletedFalse(userId);

        // 3. Kitaplar tablosu güncellemesi: isDeleted olmadığı için sadece ID ile sayıyoruz
        long booksCount = libraryItemRepository.countByUser_Id(userId);

        // 4. Görevler tablosu güncellemesi: countByUserId metodunu kullanarak performanslı sayım yapıyoruz
        long tasksCount = projectTaskRepository.countByUserId(userId);

        // 5. Abonelik ücreti toplamı
        Double totalCost = subscriptionRepository.getTotalSubscriptionCostByUserId(userId);
        if (totalCost == null) totalCost = 0.0;

        // 6. DTO paketini döndürüyoruz
        return new DashboardSummaryDTO(
                user.getUsername(),
                notesCount,
                booksCount,
                tasksCount,
                totalCost
        );
    }
}