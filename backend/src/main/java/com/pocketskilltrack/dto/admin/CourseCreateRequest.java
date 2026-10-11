package com.pocketskilltrack.dto.admin;

import com.pocketskilltrack.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseCreateRequest {

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Course slug is required")
    private String slug;

    private String description;

    @NotBlank(message = "Course category is required")
    private String category;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel = DifficultyLevel.BEGINNER;

    private String thumbnailUrl;
    private boolean published = true;

    public CourseCreateRequest() {}

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
}
