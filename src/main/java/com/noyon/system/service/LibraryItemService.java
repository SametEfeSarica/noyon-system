package com.noyon.system.service;

import com.noyon.system.dto.LibraryItemDto;
import com.noyon.system.entity.LibraryItem;
import com.noyon.system.entity.User;
import com.noyon.system.mapper.LibraryItemMapper;
import com.noyon.system.repository.LibraryItemRepository;
import com.noyon.system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryItemService {

    private final LibraryItemRepository libraryItemRepository;
    private final UserRepository         userRepository;
    private final LibraryItemMapper      mapper;

    // ── Yardımcı ──────────────────────────────────────────────────────────────

    private LibraryItem findActiveById(Long id) {
        return libraryItemRepository.findById(id)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Kitap bulunamadı: " + id));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + userId));
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /**
     * Yeni kitap ekler.
     */
    @Transactional
    public LibraryItemDto create(LibraryItemDto.Request request, Long userId) {
        User user = findUserById(userId);
        LibraryItem entity = mapper.toEntity(request);
        entity.setUser(user);
        LibraryItem saved = libraryItemRepository.save(entity);
        log.info("Kitap oluşturuldu: id={}, kullanıcı={}", saved.getId(), userId);
        return mapper.toDto(saved);
    }

    /**
     * Kitabı günceller.
     */
    @Transactional
    public LibraryItemDto update(Long id, LibraryItemDto.Request request) {
        LibraryItem entity = findActiveById(id);
        mapper.updateEntity(entity, request);
        LibraryItem saved = libraryItemRepository.save(entity);
        log.info("Kitap güncellendi: id={}", id);
        return mapper.toDto(saved);
    }

    /**
     * Kullanıcıya ait tüm aktif kitapları döndürür.
     */
    @Transactional(readOnly = true)
    public List<LibraryItemDto> getAllByUser(Long userId) {
        List<LibraryItem> items = libraryItemRepository.findByUserIdAndDeletedFalse(userId);
        return libraryItemRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Başlığa göre arama (silinmemiş).
     */
    @Transactional(readOnly = true)
    public List<LibraryItemDto> searchByTitle(String title) {
        return libraryItemRepository.findByTitleContainingIgnoreCaseAndDeletedFalse(title)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Favori durumunu toggle eder (Optimistic UI'ı destekler).
     * Yeni favori durumunu döndürür.
     */
    @Transactional
    public boolean toggleFavorite(Long id) {
        LibraryItem entity = findActiveById(id);
        boolean newState = !entity.isFavorite();
        entity.setFavorite(newState);
        libraryItemRepository.save(entity);
        log.info("Favori değişti: id={}, yeniDurum={}", id, newState);
        return newState;
    }

    /**
     * Soft delete — kitabı çöpe taşır.
     */
    @Transactional
    public void softDelete(Long id) {
        LibraryItem entity = findActiveById(id);
        entity.setDeleted(true);
        libraryItemRepository.save(entity);
        log.info("Kitap çöpe taşındı: id={}", id);
    }

    /**
     * Çöpten geri yükler.
     */
    @Transactional
    public LibraryItemDto restore(Long id) {
        LibraryItem entity = libraryItemRepository.findById(id)
                .filter(LibraryItem::isDeleted)
                .orElseThrow(() -> new EntityNotFoundException("Çöpteki kitap bulunamadı: " + id));
        entity.setDeleted(false);
        return mapper.toDto(libraryItemRepository.save(entity));
    }

    /**
     * Çöp kutusunu listeler.
     */
    @Transactional(readOnly = true)
    public List<LibraryItemDto> getTrash(Long userId) {
        List<LibraryItem> trashedItems = libraryItemRepository.findByUserIdAndDeletedTrue(userId);
        return libraryItemRepository.findByUserIdAndDeletedTrue(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Kitabı veritabanından tamamen uçurur (Kalıcı Silme).
     */
    @Transactional
    public void permanentDelete(Long id) {
        libraryItemRepository.deleteById(id);
        log.info("Kitap kalıcı olarak silindi: id={}", id);
    }
}