package com.pocketskilltrack.dto.quiz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizSubmissionResponse {

    private Long attemptId;
    private Long topicId;
    private String topicTitle;
    private int score;
    private int totalPoints;
    private double percentage;
    private boolean passed;
    private int timeSpentSeconds;
    private LocalDateTime completedTime;
    private List<QuestionResultDto> questionResults = new ArrayList<>();

    public QuizSubmissionResponse() {}

    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

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

    public List<QuestionResultDto> getQuestionResults() { return questionResults; }
    public void setQuestionResults(List<QuestionResultDto> questionResults) { this.questionResults = questionResults; }
}
