package com.pocketskilltrack.dto.course;

import java.time.LocalDateTime;

public class EnrollmentDto {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseCategory;
    private String courseThumbnailUrl;
    private int progressPercent;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    public EnrollmentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getCourseCategory() { return courseCategory; }
    public void setCourseCategory(String courseCategory) { this.courseCategory = courseCategory; }

    public String getCourseThumbnailUrl() { return courseThumbnailUrl; }
    public void setCourseThumbnailUrl(String courseThumbnailUrl) { this.courseThumbnailUrl = courseThumbnailUrl; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
