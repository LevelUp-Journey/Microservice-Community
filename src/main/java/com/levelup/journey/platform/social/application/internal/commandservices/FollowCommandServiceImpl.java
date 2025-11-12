package com.levelup.journey.platform.social.application.internal.commandservices;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.commands.CreateFollowCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveFollowByUsersCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveFollowCommand;
import com.levelup.journey.platform.social.domain.model.repositories.FollowRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.services.FollowCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Follow Command Service Implementation
 * Handles commands for Follow aggregate
 */
@Service
public class FollowCommandServiceImpl implements FollowCommandService {

    private static final Logger logger = LoggerFactory.getLogger(FollowCommandServiceImpl.class);

    private final FollowRepository followRepository;

    public FollowCommandServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public Optional<Follow> handle(CreateFollowCommand command) {
        // Generate a new UUID for the follow relationship
        FollowId followId = FollowId.of(UUID.randomUUID().toString());

        logger.info("Processing CreateFollowCommand for generated follow ID: {}, follower: {}, following: {}",
                followId.value(), command.followerId(), command.followingId());

        try {
            // Check if follow relationship already exists
            var existingRelationship = followRepository.findByFollowerIdAndFollowingId(
                    command.followerId(),
                    command.followingId()
            );
            if (existingRelationship.isPresent()) {
                logger.warn("Follow relationship already exists between {} and {}",
                        command.followerId(), command.followingId());
                throw new IllegalArgumentException("La relación de seguimiento ya existe");
            }

            // Create new follow
            Follow follow = Follow.create(
                    followId,
                    command.followerId(),
                    command.followingId()
            );

            // Save follow
            Follow savedFollow = followRepository.save(follow);
            logger.info("Follow created and saved successfully with ID: {}", savedFollow.id());

            return Optional.of(savedFollow);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in CreateFollowCommand for follower: {} and following: {} - {}",
                        command.followerId(), command.followingId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing CreateFollowCommand for follower: {} and following: {}",
                        command.followerId(), command.followingId(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(RemoveFollowCommand command) {
        logger.info("Processing RemoveFollowCommand for follow ID: {}", command.id());

        try {
            // Check if follow exists
            var existingFollow = followRepository.findById(command.id());
            if (existingFollow.isEmpty()) {
                logger.warn("Attempted to remove non-existent follow with ID: {}", command.id());
                return false;
            }

            // Delete follow
            followRepository.deleteById(command.id());
            logger.info("Follow removed successfully with ID: {}", command.id());

            return true;

        } catch (Exception e) {
            logger.error("Unexpected error processing RemoveFollowCommand for ID: {}", command.id(), e);
            return false;
        }
    }

    @Override
    public boolean handle(RemoveFollowByUsersCommand command) {
        logger.info("Processing RemoveFollowByUsersCommand for follower: {}, following: {}",
                command.followerId(), command.followingId());

        try {
            // Check if follow exists
            var existingFollow = followRepository.findByFollowerIdAndFollowingId(command.followerId(), command.followingId());
            if (existingFollow.isEmpty()) {
                logger.warn("Attempted to remove non-existent follow relationship between {} and {}",
                        command.followerId(), command.followingId());
                return false;
            }

            // Delete follow
            followRepository.deleteById(existingFollow.get().id());
            logger.info("Follow removed successfully between {} and {}", command.followerId(), command.followingId());

            return true;

        } catch (Exception e) {
            logger.error("Unexpected error processing RemoveFollowByUsersCommand for follower: {} and following: {}",
                    command.followerId(), command.followingId(), e);
            return false;
        }
    }
}
