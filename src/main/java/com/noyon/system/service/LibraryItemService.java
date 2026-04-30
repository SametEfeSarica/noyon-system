package com.noyon.system.service;

import com.noyon.system.entity.LibraryItem;
import com.noyon.system.repository.LibraryItemRepository;
import com.noyon.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryItemService {

    private final LibraryItemRepository libraryItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public LibraryItem createItem(LibraryItem item) {
        return libraryItemRepository.save(item);
    }

    @Transactional
    public LibraryItem createItemManual(LibraryItem item, Long userId) {
        log.info("Kullanıcı ID: {} için manuel kitap eşleştirmesi yapılıyor...", userId);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hata: ID'si " + userId + " olan kullanıcı bulunamadı!"));
        item.setUser(user);
        return libraryItemRepository.save(item);
    }

    // --- GÜNCELLEME (UPDATE) METODU ---
    @Transactional
    public LibraryItem updateItem(Long id, LibraryItem newItemData) {
        LibraryItem existingItem = libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek kitap bulunamadı!"));

        // Eski alanlar
        existingItem.setTitle(newItemData.getTitle());
        existingItem.setAuthor(newItemData.getAuthor());
        existingItem.setStatus(newItemData.getStatus());
        existingItem.setDueDate(newItemData.getDueDate());

        // PM'in istediği yeni alanlar
        existingItem.setCoverImage(newItemData.getCoverImage());
        existingItem.setRating(newItemData.getRating());
        existingItem.setFavorite(newItemData.isFavorite());
        existingItem.setGenre(newItemData.getGenre());
        existingItem.setFolder(newItemData.getFolder());

        return libraryItemRepository.save(existingItem);
    }

    public List<LibraryItem> getItemsByUserId(Long userId) {
        return libraryItemRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    public List<LibraryItem> getTrashedItemsByUserId(Long userId) {
        return libraryItemRepository.findByUser_IdAndIsDeletedTrue(userId);
    }

    public String deleteItem(Long id) {
        LibraryItem item = libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));
        item.setDeleted(true); // Soft delete kuralı
        libraryItemRepository.save(item);
        return "Kitap çöp kutusuna taşındı, ID: " + id;
    }

    public String restoreItem(Long id) {
        LibraryItem item = libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));
        item.setDeleted(false);
        libraryItemRepository.save(item);
        return "Kitap geri yüklendi, ID: " + id;
    }

    public List<LibraryItem> searchByTitle(String title) {
        return libraryItemRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<LibraryItem> getByStatus(String status) {
        return libraryItemRepository.findByStatus(status);
    }
}