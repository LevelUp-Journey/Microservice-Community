package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;

/**
 * Command to remove a follow relationship
 */
public record RemoveFollowCommand(FollowId id) {
    public RemoveFollowCommand {
        if (id == null) {
            throw new IllegalArgumentException("Follow id is required");
        }
    }
}
