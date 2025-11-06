package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;

/**
 * Command to remove a subscription
 */
public record RemoveSubscriptionCommand(SubscriptionId id) {
    public RemoveSubscriptionCommand {
        if (id == null) {
            throw new IllegalArgumentException("Subscription id is required");
        }
    }
}
