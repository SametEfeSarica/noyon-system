package com.noyon.system.controller;

import com.noyon.system.entity.LibraryItem;
import com.noyon.system.service.LibraryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*") // Frontend'in erişebilmesi için şart!
public class LibraryItemController {

    @Autowired
    private LibraryItemService libraryItemService;

    @PostMapping("/add")
    public ResponseEntity<LibraryItem> addLibraryItem(@RequestBody LibraryItem item) {
        return ResponseEntity.ok(libraryItemService.createItem(item));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LibraryItem>> getLibraryItems(@PathVariable Long userId) {
        return ResponseEntity.ok(libraryItemService.getItemsByUserId(userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLibraryItem(@PathVariable Long id) {
        return ResponseEntity.ok(libraryItemService.deleteItem(id));
    }
}