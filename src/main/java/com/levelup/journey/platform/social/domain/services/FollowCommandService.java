package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.commands.CreateFollowCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveFollowCommand;

import java.util.Optional;

/**
 * Follow Command Service
 * Defines business operations for managing follow relationships
 */
public interface FollowCommandService {

    /**
     * Handles the command to create a new follow relationship
     * @param command the create follow command
     * @return the created follow relationship
     */
    Optional<Follow> handle(CreateFollowCommand command);

    /**
     * Handles the command to remove a follow relationship
     * @param command the remove follow command
     * @return true if removed successfully
     */
    boolean handle(RemoveFollowCommand command);
}
