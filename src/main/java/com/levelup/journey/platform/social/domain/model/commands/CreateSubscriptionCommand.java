package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Command to create a subscription to a community
 */
public record CreateSubscriptionCommand(
        UserId userId,
        CommunityId communityId
) {
    public CreateSubscriptionCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (communityId == null) {
            throw new IllegalArgumentException("Community id is required");
        }
    }
}
