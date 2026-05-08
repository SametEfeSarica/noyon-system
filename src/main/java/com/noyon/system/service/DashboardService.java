package com.noyon.system.service;

import com.noyon.system.dto.DashboardSummaryDTO;
import com.noyon.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final SubscriptionRepository subscriptionRepository;

    public DashboardSummaryDTO getDashboardSummary(Long userId) {
        // 1. Kullanıcıyı bul
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // 2. Sayısal verileri çek
        long notesCount = noteRepository.countByUserIdAndDeletedFalse(userId);
        long tasksCount = projectTaskRepository.countByUserId(userId);
        long booksCount = libraryItemRepository.countActiveBooksByUserId(userId);

        // 3. SON 4 NOTU GETİR (Yeni eklendi)
        var recentNotes = noteRepository.findByUserIdAndDeletedFalse(
                        userId, PageRequest.of(0, 4, Sort.by("updatedAt").descending()))
                .stream()
                .map(n -> DashboardSummaryDTO.RecentNoteDTO.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .color(n.getColor())
                        .pinned(n.isPinned())
                        .updatedAt(n.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        // 4. SON 5 KİTABI GETİR
        var recentBooks = libraryItemRepository.findRecentActiveBooksByUserId(
                        userId, PageRequest.of(0, 5, Sort.by("id").descending()))
                .stream()
                .map(b -> DashboardSummaryDTO.RecentBookDTO.builder()
                        .id(b.getId())
                        .title(b.getTitle())
                        .author(b.getAuthor())
                        .build())
                .collect(Collectors.toList());

        // 5. SON 5 GÖREVİ GETİR
        var recentTasks = projectTaskRepository.findByUserId(
                        userId, PageRequest.of(0, 5, Sort.by("id").descending()))
                .stream()
                .map(t -> DashboardSummaryDTO.RecentTaskDTO.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .priority(t.getPriority())
                        .status("TODO")
                        .build())
                .collect(Collectors.toList());

        // 6. Toplam Gider Hesabı
        Double totalCost = subscriptionRepository.getTotalSubscriptionCostByUserId(userId);

        // 7. DTO'yu inşa et ve gönder
        return DashboardSummaryDTO.builder()
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .notesCount(notesCount)
                .booksCount(booksCount) // Artık doğru sayı gelecek
                .tasksCount(tasksCount)
                .totalMonthlyCost(totalCost != null ? totalCost : 0.0)
                .recentNotes(recentNotes) // Notlar listesi eklendi
                .recentBooks(recentBooks)
                .recentTasks(recentTasks)
                .upcomingPayments(new java.util.ArrayList<>())
                .build();
    }
}