package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REPLACED: entity/Folder.java
 *
 * PROBLEMS FIXED:
 *
 * 1. @Data on JPA entity — same equals/hashCode problem as Note.
 *    Replaced with explicit Lombok annotations.
 *
 * 2. cascade = CascadeType.ALL without orphanRemoval
 *    Old code had CascadeType.ALL on notes but no orphanRemoval = true.
 *    This meant removing a note from the folder's collection would NOT
 *    delete the note row — it would just null the foreign key, creating
 *    orphaned notes with no folder reference. Added orphanRemoval = true.
 *    (Note: soft-deleting notes should still be done via NoteService —
 *    this prevents hard-delete orphans from cascade operations.)
 *
 * 3. List<Note> was not initialized
 *    Accessing folder.getNotes() on a new Folder() threw NullPointerException.
 */
@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Note> notes = new ArrayList<>();
}