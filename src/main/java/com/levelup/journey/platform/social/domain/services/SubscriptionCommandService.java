package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.commands.CreateSubscriptionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveSubscriptionCommand;

import java.util.Optional;

/**
 * Subscription Command Service
 * Defines business operations for managing subscriptions
 */
public interface SubscriptionCommandService {

    /**
     * Handles the command to create a new subscription
     * @param command the create subscription command
     * @return the created subscription
     */
    Optional<Subscription> handle(CreateSubscriptionCommand command);

    /**
     * Handles the command to remove a subscription
     * @param command the remove subscription command
     * @return true if removed successfully
     */
    boolean handle(RemoveSubscriptionCommand command);
}
