package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_achievements", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_achievement", columnNames = {"user_id", "achievement_id"})
})
public class StudentAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "studentAchievement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AchievementReaction> reactions = new ArrayList<>();

    @OneToMany(mappedBy = "studentAchievement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AchievementComment> comments = new ArrayList<>();

    public StudentAchievement() {}

    public StudentAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.unlockedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Achievement getAchievement() { return achievement; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public List<AchievementReaction> getReactions() { return reactions; }
    public void setReactions(List<AchievementReaction> reactions) { this.reactions = reactions; }

    public List<AchievementComment> getComments() { return comments; }
    public void setComments(List<AchievementComment> comments) { this.comments = comments; }
}
