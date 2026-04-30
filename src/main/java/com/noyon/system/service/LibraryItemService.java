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

    // Sadece aktif (silinmemiş) kitapları getirir
    public List<LibraryItem> getItemsByUserId(Long userId) {
        return libraryItemRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    // Çöp kutusundaki kitapları getirir
    public List<LibraryItem> getTrashedItemsByUserId(Long userId) {
        return libraryItemRepository.findByUser_IdAndIsDeletedTrue(userId);
    }

    // Fiziksel silme yerine isDeleted = true yapıyoruz
    public String deleteItem(Long id) {
        LibraryItem item = libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));
        item.setDeleted(true);
        libraryItemRepository.save(item);
        return "Kitap çöp kutusuna taşındı, ID: " + id;
    }

    // Çöpten geri yükleme işlemi
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