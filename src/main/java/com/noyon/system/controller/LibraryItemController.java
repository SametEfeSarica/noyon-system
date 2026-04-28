package com.noyon.system.controller;

import com.noyon.system.entity.LibraryItem;
import com.noyon.system.service.LibraryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
@Tag(name = "Kütüphane Yönetimi")
public class LibraryItemController {

    @Autowired
    private LibraryItemService libraryItemService;

    @Operation(summary = "Yeni kitap ekler")
    @PostMapping("/add")
    public ResponseEntity<LibraryItem> addLibraryItem(@RequestBody LibraryItem item) {
        return ResponseEntity.ok(libraryItemService.createItem(item));
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
}