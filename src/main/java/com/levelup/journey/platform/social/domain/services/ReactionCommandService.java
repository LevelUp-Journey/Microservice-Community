package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.commands.CreateReactionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionCommand;

import java.util.Optional;

/**
 * Reaction Command Service
 * Defines business operations for managing reactions
 */
public interface ReactionCommandService {

    /**
     * Handles the command to create a new reaction
     * @param command the create reaction command
     * @return the created reaction
     */
    Optional<Reaction> handle(CreateReactionCommand command);

    /**
     * Handles the command to remove a reaction
     * @param command the remove reaction command
     * @return true if removed successfully
     */
    boolean handle(RemoveReactionCommand command);
}
