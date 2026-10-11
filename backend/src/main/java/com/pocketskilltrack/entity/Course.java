package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_course_category", columnList = "category"),
    @Index(name = "idx_course_slug", columnList = "slug")
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(unique = true, nullable = false, length = 180)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 80)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 30)
    private DifficultyLevel difficultyLevel = DifficultyLevel.BEGINNER;

    @Column(name = "thumbnail_url", length = 300)
    private String thumbnailUrl;

    @Column(nullable = false)
    private boolean published = true;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("moduleOrder ASC")
    private List<CourseModule> modules = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Course() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public List<CourseModule> getModules() { return modules; }
    public void setModules(List<CourseModule> modules) { this.modules = modules; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
