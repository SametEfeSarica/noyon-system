package com.noyon.system.service;

import com.noyon.system.entity.LibraryItem;
import com.noyon.system.repository.LibraryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LibraryItemService {

    @Autowired
    private LibraryItemRepository libraryItemRepository;

    public LibraryItem createItem(LibraryItem item) {
        return libraryItemRepository.save(item);
    }

    public List<LibraryItem> getItemsByUserId(Long userId) {
        // BURAYI DA GÜNCELLEDİK: Repository'deki yeni isme uyarladık
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