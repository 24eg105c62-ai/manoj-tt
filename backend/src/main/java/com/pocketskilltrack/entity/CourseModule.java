package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_modules")
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "module_order", nullable = false)
    private int moduleOrder = 1;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("topicOrder ASC")
    private List<Topic> topics = new ArrayList<>();

    public CourseModule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getModuleOrder() { return moduleOrder; }
    public void setModuleOrder(int moduleOrder) { this.moduleOrder = moduleOrder; }

    public List<Topic> getTopics() { return topics; }
    public void setTopics(List<Topic> topics) { this.topics = topics; }
}
