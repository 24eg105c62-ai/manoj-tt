package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnore
    private CourseModule module;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "reading_time_minutes", nullable = false)
    private int readingTimeMinutes = 5;

    @Column(name = "topic_order", nullable = false)
    private int topicOrder = 1;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public Topic() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CourseModule getModule() { return module; }
    public void setModule(CourseModule module) { this.module = module; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getReadingTimeMinutes() { return readingTimeMinutes; }
    public void setReadingTimeMinutes(int readingTimeMinutes) { this.readingTimeMinutes = readingTimeMinutes; }

    public int getTopicOrder() { return topicOrder; }
    public void setTopicOrder(int topicOrder) { this.topicOrder = topicOrder; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
