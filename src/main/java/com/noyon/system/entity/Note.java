package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String folderName;

    private String color;

    // Soft delete kontrol sütunu (Varsayılan olarak silinmemiş)
    private boolean isDeleted = false;

    // --- İlişkiler ---
    @ManyToOne
    @JoinColumn(name = "user_id")
    // BURADAKİ @JsonIgnore KALDIRILDI! (Artık dışarıdan id:1 bilgisi içeri girebilecek)
    private User user;

    // --- Getter ve Setterlar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFolderName() { return folderName; }
    public void setFolderName(String folderName) { this.folderName = folderName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}