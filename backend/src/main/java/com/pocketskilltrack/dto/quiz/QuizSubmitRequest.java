package com.pocketskilltrack.dto.quiz;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class QuizSubmitRequest {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    private int timeSpentSeconds;

    // Map of questionId -> selectedOptionId
    private Map<Long, Long> answers = new HashMap<>();

    public QuizSubmitRequest() {}

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public Map<Long, Long> getAnswers() { return answers; }
    public void setAnswers(Map<Long, Long> answers) { this.answers = answers; }
}
