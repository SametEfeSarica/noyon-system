package com.noyon.system.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "library_items")
 // Eğer hata verirse Getter ve Setter'ları elle oluşturmasını söyle
public class LibraryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String status; // ELIMDE, ALINACAK, KIRALIK
    private LocalDate dueDate; // Teslim tarihi
    private Long userId; // Hangi kullanıcıya ait

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}