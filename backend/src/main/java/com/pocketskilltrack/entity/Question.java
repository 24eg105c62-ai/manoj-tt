package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private Topic topic;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column(nullable = false)
    private int points = 10;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("optionOrder ASC")
    private List<AnswerOption> options = new ArrayList<>();

    public Question() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<AnswerOption> getOptions() { return options; }
    public void setOptions(List<AnswerOption> options) { this.options = options; }
}
