package com.pocketskilltrack.dto.course;

import com.pocketskilltrack.entity.DifficultyLevel;
import java.util.ArrayList;
import java.util.List;

public class CourseDetailDto {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private String category;
    private DifficultyLevel difficultyLevel;
    private String thumbnailUrl;
    private boolean enrolled;
    private int progressPercent;
    private boolean bookmarked;
    private int totalTopics;
    private int completedTopics;
    private List<CourseModuleDto> modules = new ArrayList<>();

    public CourseDetailDto() {}

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

    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public boolean isBookmarked() { return bookmarked; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }

    public int getTotalTopics() { return totalTopics; }
    public void setTotalTopics(int totalTopics) { this.totalTopics = totalTopics; }

    public int getCompletedTopics() { return completedTopics; }
    public void setCompletedTopics(int completedTopics) { this.completedTopics = completedTopics; }

    public List<CourseModuleDto> getModules() { return modules; }
    public void setModules(List<CourseModuleDto> modules) { this.modules = modules; }
}
