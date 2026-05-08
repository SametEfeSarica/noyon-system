package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * LibraryItem entity — genişletilmiş versiyon.
 * <p>
 * Frontend (Library.jsx) şu alanları bekliyor:
 * id, title, author, category, pages, progress,
 * colorStart, colorEnd (color[0]/color[1] yerine),
 * accent, spine, cover, year, description,
 * tags (List<String>), rating, isFavorite
 * <p>
 * DB'de tags virgülle ayrılmış String olarak saklanır,
 * DTO katmanında List<String>'e dönüştürülür.
 */
@Entity
@Table(name = "user_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String categoryId;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 20)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}
