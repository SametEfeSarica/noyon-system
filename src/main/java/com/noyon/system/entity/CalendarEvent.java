package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importu ekledik
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_events")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime date;
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Bu satır sayesinde şifre vb. veriler JSON çıktısında gizlenir
    private User user;

    // --- Getter ve Setterlar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}