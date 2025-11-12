package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.commands.AddReactionCommand;
import com.levelup.journey.platform.social.domain.model.commands.CreateReactionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionByUserAndPostCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionsByPostCommand;

import java.util.Optional;

/**
 * Reaction Command Service
 * Defines business operations for managing reactions
 */
public interface ReactionCommandService {

    /**
     * Handles the command to create a new reaction (toggle behavior)
     * @param command the create reaction command
     * @return the created reaction
     */
    Optional<Reaction> handle(CreateReactionCommand command);

    /**
     * Handles the command to add a new reaction
     * Only creates if user hasn't reacted to this post yet
     * @param command the add reaction command
     * @return the created reaction
     * @throws IllegalStateException if user already has a reaction on this post
     */
    Optional<Reaction> handle(AddReactionCommand command);

    /**
     * Handles the command to remove a reaction
     * @param command the remove reaction command
     * @return true if removed successfully
     */
    boolean handle(RemoveReactionCommand command);

    /**
     * Handles the command to remove a reaction by user and post
     * @param command the remove reaction by user and post command
     * @return true if removed successfully
     */
    boolean handle(RemoveReactionByUserAndPostCommand command);

    /**
     * Handles the command to remove all reactions for a post
     * @param command the remove reactions by post command
     * @return true if removed successfully
     */
    boolean handle(RemoveReactionsByPostCommand command);
}
