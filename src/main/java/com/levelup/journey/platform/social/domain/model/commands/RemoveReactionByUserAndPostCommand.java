package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Command to remove a reaction by user and post
 */
public record RemoveReactionByUserAndPostCommand(PostId postId, UserId userId) {
    public RemoveReactionByUserAndPostCommand {
        if (postId == null) {
            throw new IllegalArgumentException("Post id is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
    }
}