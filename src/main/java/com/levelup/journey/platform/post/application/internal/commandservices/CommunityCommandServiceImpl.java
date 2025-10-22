package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.services.CommunityCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
        // Generate a new UUID for the community
        CommunityId communityId = CommunityId.of(UUID.randomUUID().toString());

        logger.info("Processing CreateCommunityCommand for generated community ID: {}, name: {}, ownerId: {}",
                   communityId.value(), command.name(), command.ownerId());

        try {
            // Check if community with this ID already exists (very unlikely with UUID)
            var existingCommunity = communityRepository.findById(communityId);
            if (existingCommunity.isPresent()) {
                logger.warn("Attempted to create community with existing ID: {}", communityId);
                throw new IllegalArgumentException("Ya existe una comunidad con el ID: " + communityId);
            }

            // Create new community from command
            Community community = Community.create(
                    communityId,
                    command.ownerId(),
                    command.name(),
                    command.description()
            );

            // Save community
            Community savedCommunity = communityRepository.save(community);
            logger.info("Community created and saved successfully with ID: {}", savedCommunity.id());

            return Optional.of(savedCommunity);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in CreateCommunityCommand for ID: {} - {}", communityId, e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing CreateCommunityCommand for ID: {}", communityId, e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }
}
