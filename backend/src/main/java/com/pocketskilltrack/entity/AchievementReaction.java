package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_reactions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_reaction_user_achievement", columnNames = {"student_achievement_id", "user_id"})
})
public class AchievementReaction {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 30)
    private ReactionType reactionType = ReactionType.LIKE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AchievementReaction() {}

    public AchievementReaction(StudentAchievement studentAchievement, User user, ReactionType reactionType) {
        this.studentAchievement = studentAchievement;
        this.user = user;
        this.reactionType = reactionType;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentAchievement getStudentAchievement() { return studentAchievement; }
    public void setStudentAchievement(StudentAchievement studentAchievement) { this.studentAchievement = studentAchievement; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ReactionType getReactionType() { return reactionType; }
    public void setReactionType(ReactionType reactionType) { this.reactionType = reactionType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
