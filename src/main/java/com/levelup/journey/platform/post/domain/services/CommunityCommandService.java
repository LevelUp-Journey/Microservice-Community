package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.commands.DeleteCommunityCommand;
import com.levelup.journey.platform.post.domain.model.commands.UpdateCommunityCommand;

import java.util.Optional;

/**
 * Community Command Service
 * Defines business operations for managing communities
 */
public interface CommunityCommandService {

    /**
     * Handles the command to create a new community
     * @param command the create community command
     * @return the created community
     */
    Optional<Community> handle(CreateCommunityCommand command);

    /**
     * Handles the command to update an existing community
     * @param command the update community command
     * @return the updated community
     */
    Optional<Community> handle(UpdateCommunityCommand command);

    /**
     * Handles the command to delete a community
     * @param command the delete community command
     * @return true if deletion was successful, false otherwise
     */
    boolean handle(DeleteCommunityCommand command);
}
