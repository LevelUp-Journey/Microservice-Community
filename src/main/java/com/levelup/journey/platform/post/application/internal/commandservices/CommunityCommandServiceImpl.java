package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.commands.DeleteCommunityCommand;
import com.levelup.journey.platform.post.domain.model.commands.UpdateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.services.CommunityCommandService;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            var imageUrl = command.imageUrl() != null ? ImageUrl.of(command.imageUrl()) : ImageUrl.empty();
            Community community = Community.create(
                    communityId,
                    command.ownerId(),
                    command.ownerProfileId(),
                    command.name(),
                    command.description(),
                    imageUrl
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

    @Override
    public Optional<Community> handle(UpdateCommunityCommand command) {
        logger.info("Processing UpdateCommunityCommand for community ID: {}, name: {}",
                   command.communityId().value(), command.name());

        try {
            // Find the community
            var communityOptional = communityRepository.findById(command.communityId());
            if (communityOptional.isEmpty()) {
                logger.warn("Attempted to update non-existent community ID: {}", command.communityId());
                throw new IllegalArgumentException("La comunidad con ID: " + command.communityId() + " no existe");
            }

            Community community = communityOptional.get();

            // Update community information
            var imageUrl = command.imageUrl() != null ? ImageUrl.of(command.imageUrl()) : ImageUrl.empty();
            community.update(
                    command.name(),
                    command.description(),
                    imageUrl
            );

            // Save updated community
            Community updatedCommunity = communityRepository.save(community);
            logger.info("Community updated successfully with ID: {}", updatedCommunity.id());

            return Optional.of(updatedCommunity);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in UpdateCommunityCommand for ID: {} - {}",
                        command.communityId(), e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing UpdateCommunityCommand for ID: {}",
                        command.communityId(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }

    @Override
    public boolean handle(DeleteCommunityCommand command) {
        logger.info("Processing DeleteCommunityCommand for community: {}, requesterId: {}",
                   command.communityId().value(), command.requesterId().value());

        try {
            // Find the community
            Optional<Community> communityOptional = communityRepository.findById(command.communityId());
            if (communityOptional.isEmpty()) {
                logger.warn("Community not found for deletion: {}", command.communityId().value());
                return false;
            }

            Community community = communityOptional.get();

            // Check authorization
            boolean isAuthorized = false;

            // Check if requester is the community owner
            if (community.ownerId().equals(command.requesterId())) {
                isAuthorized = true;
                logger.debug("User {} is community owner, allowing deletion", command.requesterId().value());
            } else {
                // Check if requester is an admin
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getAuthorities() != null) {
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
                    if (isAdmin) {
                        isAuthorized = true;
                        logger.debug("User {} is admin, allowing deletion", command.requesterId().value());
                    }
                }
            }

            if (!isAuthorized) {
                logger.warn("User {} attempted to delete community {} but lacks permission",
                           command.requesterId().value(), command.communityId().value());
                throw new IllegalArgumentException("No tienes permiso para eliminar esta comunidad. Solo el propietario de la comunidad o un administrador pueden eliminar comunidades.");
            }

            // Delete the community
            communityRepository.deleteById(command.communityId());
            logger.info("Community deleted successfully: {}", command.communityId().value());

            return true;

        } catch (IllegalArgumentException e) {
            logger.error("Authorization error in DeleteCommunityCommand for community: {} - {}",
                        command.communityId().value(), e.getMessage());
            throw e; // Re-throw authorization errors
        } catch (Exception e) {
            logger.error("Unexpected error processing DeleteCommunityCommand for community: {}",
                        command.communityId().value(), e);
            return false; // Return false for unexpected errors
        }
    }
}
