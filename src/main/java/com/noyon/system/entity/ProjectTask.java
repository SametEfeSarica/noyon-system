package com.noyon.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 10)
    @Builder.Default
    private String priority = "medium";

    private LocalDate dueDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;

    // DÜZELTME: Veritabanındaki is_deleted sütunu ile eşleşecek alan eklendi.
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    @JsonIgnore
    private TaskColumn column;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> assignees = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_labels", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "label", length = 60)
    @Builder.Default
    private List<String> labels = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ChecklistItem> checklist = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TaskComment> comments = new ArrayList<>();

    // DÜZELTME: Veritabanına yazılmadan hemen önce Null kontrolü yapan HAYAT KURTARICI zırh
    @PrePersist
    public void prePersist() {
        if (this.position == null) {
            this.position = 0;
        }
        if (this.priority == null) {
            this.priority = "medium";
        }
        // DÜZELTME: is_deleted hatasını engellemek için null kontrolü
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }
}