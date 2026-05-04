package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REPLACED: entity/User.java
 *
 * PROBLEMS FIXED:
 *
 * 1. ProjectTask relationship was missing
 *    User owned ProjectTask records but the reverse OneToMany was not declared.
 *    Without it, CascadeType.ALL cannot propagate deletions, leaving orphan tasks
 *    in the DB when a user is deleted.
 *
 * 2. CalendarEvent relationship was missing the cascade declaration
 *    Old code had the OneToMany but no cascade — deleting a user left orphan events.
 *
 * 3. Password field had no @Column(nullable=false)
 *    A null password would be silently accepted by JPA and cause a DB constraint
 *    error instead of a clean validation error.
 *
 * All collection fields use @Builder.Default so they are initialized even when
 * the Builder is used without setting them, preventing NullPointerException.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    /** BCrypt hash — never returned in any DTO or response */
    @NotBlank
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LibraryItem> libraryItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Subscription> subscriptions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CalendarEvent> calendarEvents = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectTask> projectTasks = new ArrayList<>();
}