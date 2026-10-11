package com.pocketskilltrack.dto.course;

import com.pocketskilltrack.entity.DifficultyLevel;

public class CourseDto {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private String category;
    private DifficultyLevel difficultyLevel;
    private String thumbnailUrl;
    private int moduleCount;
    private int topicCount;
    private boolean enrolled;
    private int progressPercent;
    private boolean bookmarked;

    public CourseDto() {}

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

    public int getModuleCount() { return moduleCount; }
    public void setModuleCount(int moduleCount) { this.moduleCount = moduleCount; }

    public int getTopicCount() { return topicCount; }
    public void setTopicCount(int topicCount) { this.topicCount = topicCount; }

    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public boolean isBookmarked() { return bookmarked; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
}
