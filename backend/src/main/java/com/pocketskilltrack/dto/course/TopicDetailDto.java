package com.pocketskilltrack.dto.course;

public class TopicDetailDto {

    private Long id;
    private String title;
    private String content;
    private int readingTimeMinutes;
    private int topicOrder;
    private boolean completed;
    private boolean quizAvailable;
    private Long courseId;
    private String courseTitle;
    private Long moduleId;
    private String moduleTitle;
    private Long nextTopicId;
    private Long prevTopicId;

    public TopicDetailDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getReadingTimeMinutes() { return readingTimeMinutes; }
    public void setReadingTimeMinutes(int readingTimeMinutes) { this.readingTimeMinutes = readingTimeMinutes; }

    public int getTopicOrder() { return topicOrder; }
    public void setTopicOrder(int topicOrder) { this.topicOrder = topicOrder; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isQuizAvailable() { return quizAvailable; }
    public void setQuizAvailable(boolean quizAvailable) { this.quizAvailable = quizAvailable; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getModuleTitle() { return moduleTitle; }
    public void setModuleTitle(String moduleTitle) { this.moduleTitle = moduleTitle; }

    public Long getNextTopicId() { return nextTopicId; }
    public void setNextTopicId(Long nextTopicId) { this.nextTopicId = nextTopicId; }

    public Long getPrevTopicId() { return prevTopicId; }
    public void setPrevTopicId(Long prevTopicId) { this.prevTopicId = prevTopicId; }
}
