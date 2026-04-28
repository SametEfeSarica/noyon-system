package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "project_tasks")
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Görev başlığı boş bırakılamaz")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // TODO, IN_PROGRESS, DONE
    private String status = "TODO";

    // LOW, MEDIUM, HIGH
    private String priority = "MEDIUM";

    private LocalDate dueDate;

    // --- İlişkiler ---
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // --- Getter ve Setterlar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}