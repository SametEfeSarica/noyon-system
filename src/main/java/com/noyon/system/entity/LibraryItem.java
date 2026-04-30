package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty; // Notasyon için gerekli import
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "library_items")
public class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kitap başlığı boş olamaz")
    private String title;

    @NotBlank(message = "Yazar adı boş olamaz")
    private String author;

    private String status;
    private LocalDate dueDate;

    // --- YENİ EKLENEN ALANLAR ---

    @Column(columnDefinition = "LONGTEXT") // Base64 fotoğraflar için devasa alan
    private String coverImage;

    private int rating;

    @JsonProperty("isFavorite") // JSON'daki ismi isFavorite olarak zorlar, eşleşme hatasını çözer
    private boolean isFavorite = false;

    private String genre;
    private String folder;

    // ----------------------------

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // Sonsuz döngü kalkanı
    private User user;

    public LibraryItem() {}

    // Getter ve Setterlar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}