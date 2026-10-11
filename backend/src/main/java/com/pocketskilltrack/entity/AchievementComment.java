package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_comments")
public class AchievementComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_achievement_id", nullable = false)
    @JsonIgnore
    private StudentAchievement studentAchievement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AchievementComment() {}

    public AchievementComment(StudentAchievement studentAchievement, User user, String commentText) {
        this.studentAchievement = studentAchievement;
        this.user = user;
        this.commentText = commentText;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentAchievement getStudentAchievement() { return studentAchievement; }
    public void setStudentAchievement(StudentAchievement studentAchievement) { this.studentAchievement = studentAchievement; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
