package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Command to create a follow relationship between two users
 */
public record CreateFollowCommand(
        FollowId id,
        UserId followerId,
        UserId followingId
) {
    public CreateFollowCommand {
        if (id == null) {
            throw new IllegalArgumentException("Follow id is required");
        }
        if (followerId == null) {
            throw new IllegalArgumentException("Follower id is required");
        }
        if (followingId == null) {
            throw new IllegalArgumentException("Following id is required");
        }
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
    }
}
