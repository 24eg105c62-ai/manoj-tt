package com.pocketskilltrack.dto.achievement;

import com.pocketskilltrack.entity.ReactionType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StudentAchievementDto {

    private Long id;
    private Long achievementId;
    private String badgeCode;
    private String title;
    private String description;
    private String iconName;
    private LocalDateTime unlockedAt;
    private Long userId;
    private String studentName;
    private String studentId;
    private long totalReactions;
    private ReactionType userReaction;
    private Map<String, Long> reactionCounts = new HashMap<>();
    private long commentCount;

    public StudentAchievementDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAchievementId() { return achievementId; }
    public void setAchievementId(Long achievementId) { this.achievementId = achievementId; }

    public String getBadgeCode() { return badgeCode; }
    public void setBadgeCode(String badgeCode) { this.badgeCode = badgeCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public long getTotalReactions() { return totalReactions; }
    public void setTotalReactions(long totalReactions) { this.totalReactions = totalReactions; }

    public ReactionType getUserReaction() { return userReaction; }
    public void setUserReaction(ReactionType userReaction) { this.userReaction = userReaction; }

    public Map<String, Long> getReactionCounts() { return reactionCounts; }
    public void setReactionCounts(Map<String, Long> reactionCounts) { this.reactionCounts = reactionCounts; }

    public long getCommentCount() { return commentCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
}
