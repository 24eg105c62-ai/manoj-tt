package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "topic_completions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_topic", columnNames = {"user_id", "topic_id"})
})
public class TopicCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();

    public TopicCompletion() {}

    public TopicCompletion(User user, Topic topic) {
        this.user = user;
        this.topic = topic;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
