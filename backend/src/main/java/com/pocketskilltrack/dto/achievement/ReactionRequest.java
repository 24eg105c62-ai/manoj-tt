package com.pocketskilltrack.dto.achievement;

import com.pocketskilltrack.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public class ReactionRequest {

    @NotNull(message = "Reaction type is required")
    private ReactionType reactionType;

    public ReactionRequest() {}

    public ReactionRequest(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public ReactionType getReactionType() { return reactionType; }
    public void setReactionType(ReactionType reactionType) { this.reactionType = reactionType; }
}
