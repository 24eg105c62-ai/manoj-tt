package com.pocketskilltrack.dto.achievement;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private Long studentAchievementId;
    private Long userId;
    private String userName;
    private String commentText;
    private LocalDateTime createdAt;
    private boolean ownComment;

    public CommentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentAchievementId() { return studentAchievementId; }
    public void setStudentAchievementId(Long studentAchievementId) { this.studentAchievementId = studentAchievementId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isOwnComment() { return ownComment; }
    public void setOwnComment(boolean ownComment) { this.ownComment = ownComment; }
}
