package com.pocketskilltrack.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    @NotBlank(message = "Comment text cannot be empty")
    @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
    private String commentText;

    public CommentRequest() {}

    public CommentRequest(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
}
