package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.services.CommunityCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Community Command Service Implementation
 * Handles commands for Community aggregate
 */
@Service
public class CommunityCommandServiceImpl implements CommunityCommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityCommandServiceImpl.class);

    private final CommunityRepository communityRepository;

    public CommunityCommandServiceImpl(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public Optional<Community> handle(CreateCommunityCommand command) {
        logger.info("Processing CreateCommunityCommand for community ID: {}, name: {}, ownerId: {}",
                   command.id(), command.name(), command.ownerId());

        try {
            // Check if community with this ID already exists
            var existingCommunity = communityRepository.findById(command.id());
            if (existingCommunity.isPresent()) {
                logger.warn("Attempted to create community with existing ID: {}", command.id());
                throw new IllegalArgumentException("Ya existe una comunidad con el ID: " + command.id());
            }

            // Create new community from command
            Community community = Community.create(
                    command.id(),
                    command.ownerId(),
                    command.name(),
                    command.description()
            );

            // Save community
            Community savedCommunity = communityRepository.save(community);
            logger.info("Community created and saved successfully with ID: {}", savedCommunity.id());

            return Optional.of(savedCommunity);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in CreateCommunityCommand for ID: {} - {}", command.id(), e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing CreateCommunityCommand for ID: {}", command.id(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }
}
