package com.pocketskilltrack.dto.course;

public class TopicDto {

    private Long id;
    private String title;
    private int readingTimeMinutes;
    private int topicOrder;
    private boolean completed;
    private boolean quizAvailable;

    public TopicDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getReadingTimeMinutes() { return readingTimeMinutes; }
    public void setReadingTimeMinutes(int readingTimeMinutes) { this.readingTimeMinutes = readingTimeMinutes; }

    public int getTopicOrder() { return topicOrder; }
    public void setTopicOrder(int topicOrder) { this.topicOrder = topicOrder; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isQuizAvailable() { return quizAvailable; }
    public void setQuizAvailable(boolean quizAvailable) { this.quizAvailable = quizAvailable; }
}
