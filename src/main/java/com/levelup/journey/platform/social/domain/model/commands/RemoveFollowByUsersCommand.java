package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Command to remove a follow relationship by follower and following user IDs
 */
public record RemoveFollowByUsersCommand(UserId followerId, UserId followingId) {
    public RemoveFollowByUsersCommand {
        if (followerId == null) {
            throw new IllegalArgumentException("Follower ID is required");
        }
        if (followingId == null) {
            throw new IllegalArgumentException("Following ID is required");
        }
    }
}