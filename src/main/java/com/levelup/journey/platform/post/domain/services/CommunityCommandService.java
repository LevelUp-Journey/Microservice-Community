package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;

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
}
