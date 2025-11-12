package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Command to add a new reaction to a post
 * Only creates if the user hasn't reacted to this post yet
 */
public record AddReactionCommand(
        PostId postId,
        UserId userId,
        ReactionType reactionType
) {
    public AddReactionCommand {
        if (postId == null) {
            throw new IllegalArgumentException("Post id is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (reactionType == null) {
            throw new IllegalArgumentException("Reaction type is required");
        }
    }
}