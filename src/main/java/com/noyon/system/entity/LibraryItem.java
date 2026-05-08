package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "library_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Temel bilgiler ──────────────────────────────────────────────────────────

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    /** design | engineering | productivity | philosophy | business */
    @Column(length = 50)
    private String category;

    private Integer pages;

    /** 0–100 arası okuma ilerlemesi */
    @Builder.Default
    private int progress = 0;

    private Integer year;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ── Görsel / UI alanlar ─────────────────────────────────────────────────────

    /** Kitap kapağının gradient başlangıç rengi, örn: "#1a1a2e" */
    @Column(name = "color_start", length = 20)
    private String colorStart;

    /** Kitap kapağının gradient bitiş rengi, örn: "#16213e" */
    @Column(name = "color_end", length = 20)
    private String colorEnd;

    /** Vurgu rengi (buton, progress bar, tag), örn: "#7c3aed" */
    @Column(length = 20)
    private String accent;

    /** Kitap sırtı (spine) rengi, örn: "#6d28d9" */
    @Column(length = 20)
    private String spine;

    /**
     * Kapak üzerindeki kısa metin/sembol (örn: "DOET", "CC", "0→1").
     * LONGTEXT'e gerek yok; base64 resim kullanılmıyor.
     */
    @Column(length = 20)
    private String cover;

    // ── Meta ────────────────────────────────────────────────────────────────────

    /**
     * Etiketler virgülle ayrılmış şekilde saklanır: "UX,Psychology"
     * DTO dönüşümünde List<String>'e parse edilir.
     */
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    /** 1–5 yıldız puanı */
    @Builder.Default
    private int rating = 0;

    @Builder.Default
    @Column(name = "is_favorite", nullable = false)
    private boolean favorite = false;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── İlişki ─────────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // ── Yardımcı metot ──────────────────────────────────────────────────────────

    /** "UX,Psychology" → ["UX","Psychology"] */
    @Transient
    public List<String> getTagList() {
        if (tags == null || tags.isBlank()) return new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (String t : tags.split(",")) {
            String trimmed = t.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }
}
