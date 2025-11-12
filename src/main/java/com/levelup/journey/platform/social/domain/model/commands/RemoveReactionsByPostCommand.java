package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;

/**
 * Command to remove all reactions for a specific post
 */
public record RemoveReactionsByPostCommand(PostId postId) {
    public RemoveReactionsByPostCommand {
        if (postId == null) {
            throw new IllegalArgumentException("Post id is required");
        }
    }
}