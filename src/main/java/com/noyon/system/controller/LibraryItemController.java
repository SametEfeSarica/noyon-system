package com.noyon.system.controller;

import com.noyon.system.entity.LibraryItem;
import com.noyon.system.service.LibraryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/library_items")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Kütüphane Yönetimi")
public class LibraryItemController {

    @Autowired
    private LibraryItemService libraryItemService;

    @Operation(summary = "Yeni kitap ekler (Kullanıcı ID ile)")
    @PostMapping("/add/{userId}") // Adresi /add/1 şeklinde kullanacağız
    public ResponseEntity<?> addLibraryItem(@PathVariable Long userId, @RequestBody LibraryItem item) {
        log.info("--- YENİ KİTAP EKLEME İSTEĞİ ---");
        log.info("Kullanıcı ID: {}, Kitap: {}", userId, item.getTitle());

        try {
            // Servis katmanına hem kitabı hem de URL'den gelen ID'yi gönderiyoruz
            LibraryItem savedItem = libraryItemService.createItemManual(item, userId);
            log.info("Kitap başarıyla kaydedildi. ID: {}", savedItem.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);

        } catch (Exception e) {
            log.error("🚨 KİTAP EKLEME HATASI: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Operation(summary = "Kullanıcıya ait kitapları listeler")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LibraryItem>> getLibraryItems(@PathVariable Long userId) {
        return ResponseEntity.ok(libraryItemService.getItemsByUserId(userId));
    }

    @Operation(summary = "Kitap ismine göre arama yapar")
    @GetMapping("/search")
    public ResponseEntity<List<LibraryItem>> searchItems(@RequestParam String title) {
        return ResponseEntity.ok(libraryItemService.searchByTitle(title));
    }

    @Operation(summary = "Duruma göre filtreleme yapar")
    @GetMapping("/filter")
    public ResponseEntity<List<LibraryItem>> filterByStatus(@RequestParam String status) {
        return ResponseEntity.ok(libraryItemService.getByStatus(status));
    }

    @Operation(summary = "Kitap siler")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLibraryItem(@PathVariable Long id) {
        return ResponseEntity.ok(libraryItemService.deleteItem(id));
    }

    // Çöp kutusundaki kitapları listeler
    @GetMapping("/trash/{userId}")
    public List<LibraryItem> getTrashedItems(@PathVariable Long userId) {
        return libraryItemService.getTrashedItemsByUserId(userId);
    }

    // Kitabı geri yükler
    @PutMapping("/restore/{id}")
    public String restoreItem(@PathVariable Long id) {
        return libraryItemService.restoreItem(id);
    }
}