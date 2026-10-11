package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts", indexes = {
    @Index(name = "idx_quiz_user", columnList = "user_id"),
    @Index(name = "idx_quiz_topic", columnList = "topic_id")
})
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @Column(nullable = false)
    private int score = 0;

    @Column(name = "total_points", nullable = false)
    private int totalPoints = 0;

    @Column(nullable = false)
    private double percentage = 0.0;

    @Column(nullable = false)
    private boolean passed = false;

    @Column(name = "time_spent_seconds", nullable = false)
    private int timeSpentSeconds = 0;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizResponse> responses = new ArrayList<>();

    public QuizAttempt() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

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

    public List<QuizResponse> getResponses() { return responses; }
    public void setResponses(List<QuizResponse> responses) { this.responses = responses; }
}
