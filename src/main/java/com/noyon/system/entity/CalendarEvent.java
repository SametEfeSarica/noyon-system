package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * REPLACED: entity/CalendarEvent.java
 *
 * PROBLEMS FIXED:
 * 1. Manual getters/setters replaced with Lombok.
 * 2. User join column had no nullable=false — a CalendarEvent with no user
 *    is meaningless and would cause NPEs in the service layer.
 * 3. title had no nullable=false constraint.
 */
@Entity
@Table(name = "calendar_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(length = 50)
    private String category;

    @Column(length = 20)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}