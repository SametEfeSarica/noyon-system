package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Dinamik Kanban sütun entity'si.
 *
 * Her kullanıcının kendine ait sütunları vardır (user_id ile ilişkili).
 * Sıralama position alanıyla tutulur — drag & drop sırası DB'de kalıcıdır.
 * Kartlar bu sütuna OneToMany ile bağlıdır.
 */
@Entity
@Table(name = "task_columns") // UniqueConstraint kaldırıldı!
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    /** Hex renk kodu — örn. "#6c6af6". Frontend renk seçiciden gelir. */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String color = "#505060";

    /** Sütunların soldan sağa sırası. 0-tabanlı. */
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;

    /**
     * Sütun sahibi kullanıcı.
     * JSON serialize edilmez — DTO katmanı sadece columnId döner.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Bu sütuna ait kartlar.
     * position alanına göre sıralı döndürülür (Repository katmanında @OrderBy).
     * cascade = ALL: sütun silinince kartlar da silinir (orphanRemoval).
     */
    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    @Builder.Default
    private List<ProjectTask> cards = new ArrayList<>();
    @Column(name = "workspace_id")
    @Builder.Default
    private Long workspaceId = 1L;
}