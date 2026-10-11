package com.pocketskilltrack.dto.quiz;

import java.time.LocalDateTime;

public class QuizAttemptDto {

    private Long id;
    private Long topicId;
    private String topicTitle;
    private String courseTitle;
    private int score;
    private int totalPoints;
    private double percentage;
    private boolean passed;
    private int timeSpentSeconds;
    private LocalDateTime completedTime;

    public QuizAttemptDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
}
