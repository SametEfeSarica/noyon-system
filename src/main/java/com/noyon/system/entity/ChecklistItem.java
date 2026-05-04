package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * REPLACED: entity/ChecklistItem.java
 *
 * PROBLEMS FIXED:
 * 1. Manual getters/setters replaced with Lombok.
 * 2. isCompleted field naming — Lombok generates isCompleted() correctly
 *    because the field already starts with "is", but Jackson can mis-serialize it.
 *    Renamed to `completed` to avoid the double-"is" serialization bug
 *    (Jackson would generate `isCompleted` from the getter, leading to
 *    { "completed": false } in JSON but "isCompleted" in the DB column).
 *    @Column name explicitly set to `is_completed` to keep the DB schema stable.
 */
@Entity
@Table(name = "task_checklists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Builder.Default
    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private ProjectTask task;
}