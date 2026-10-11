package com.pocketskilltrack.dto.course;

import java.util.ArrayList;
import java.util.List;

public class CourseModuleDto {

    private Long id;
    private String title;
    private int moduleOrder;
    private List<TopicDto> topics = new ArrayList<>();

    public CourseModuleDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getModuleOrder() { return moduleOrder; }
    public void setModuleOrder(int moduleOrder) { this.moduleOrder = moduleOrder; }

    public List<TopicDto> getTopics() { return topics; }
    public void setTopics(List<TopicDto> topics) { this.topics = topics; }
}
