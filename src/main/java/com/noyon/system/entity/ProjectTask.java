package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * GÜNCELLENDİ: entity/ProjectTask.java  (v2 — dinamik sütun desteği)
 *
 * DEĞİŞİKLİKLER:
 *
 * 1. status alanı KALDIRILDI.
 *    Artık "TODO / IN_PROGRESS / DONE" string'i yok.
 *    Kartın durumu hangi sütunda olduğuyla belirlenir (column ilişkisi).
 *    Bu mimari değişiklik drag & drop'u çok basitleştirir:
 *    kart taşımak = column FK'yı değiştirmek.
 *
 * 2. column (TaskColumn) ManyToOne ilişkisi EKLENDİ.
 *    @JsonIgnore ile sonsuz döngü önlendi.
 *
 * 3. position alanı EKLENDİ.
 *    Sütun içindeki kart sırası için (0-tabanlı).
 *    Drag & drop sırası bu alan sayesinde kalıcıdır.
 *
 * 4. labels alanı EKLENDİ.
 *    Frontend'deki LabelChip'leri destekler.
 *    ElementCollection ile basit string listesi olarak saklanır.
 *
 * 5. priority değerleri frontend ile uyumlu hale getirildi:
 *    "urgent" | "high" | "medium" | "low"  (eskisi: LOW/MED/HIGH)
 */
@Entity
@Table(name = "project_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Öncelik: urgent | high | medium | low
     * Service katmanında validate edilir.
     */
    @Column(length = 10)
    @Builder.Default
    private String priority = "medium";

    private LocalDate dueDate;

    /** Sütun içindeki sıra (0-tabanlı). Drag & drop ile güncellenir. */
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;

    // ── İlişkiler ─────────────────────────────────────────────────────────────

    /** Kartın sahibi sütun. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    @JsonIgnore
    private TaskColumn column;

    /** Pano sahibi kullanıcı — hızlı sorgular için denormalize FK. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Atanan kullanıcılar.
     * DTO katmanı sadece userId + username döndürür.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> assignees = new ArrayList<>();

    /**
     * Etiketler — basit string listesi.
     * Ayrı entity'ye gerek yok, string koleksiyonu yeterli.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "task_labels", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "label", length = 60)
    @Builder.Default
    private List<String> labels = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChecklistItem> checklist = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TaskComment> comments = new ArrayList<>();
}
