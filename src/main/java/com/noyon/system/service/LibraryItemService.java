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
    private final UserRepository userRepository; // Kullanıcıyı bulmak için bu satır şart

    @Transactional
    public LibraryItem createItem(LibraryItem item) {
        return libraryItemRepository.save(item);
    }

    // İŞTE ÇÖZÜM: Controller'ın aradığı metot tam olarak bu
    @Transactional
    public LibraryItem createItemManual(LibraryItem item, Long userId) {
        log.info("Kullanıcı ID: {} için manuel kitap eşleştirmesi yapılıyor...", userId);

        // 1. Kullanıcıyı DB'den buluyoruz
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hata: ID'si " + userId + " olan kullanıcı bulunamadı!"));

        // 2. Kitaba kullanıcıyı bizzat biz bağlıyoruz
        item.setUser(user);

        // 3. Kaydediyoruz
        return libraryItemRepository.save(item);
    }

    public List<LibraryItem> getItemsByUserId(Long userId) {
        return libraryItemRepository.findByUser_Id(userId);
    }

    public String deleteItem(Long id) {
        libraryItemRepository.deleteById(id);
        return "Kitap silindi, ID: " + id;
    }

    public List<LibraryItem> searchByTitle(String title) {
        return libraryItemRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<LibraryItem> getByStatus(String status) {
        return libraryItemRepository.findByStatus(status);
    }
}