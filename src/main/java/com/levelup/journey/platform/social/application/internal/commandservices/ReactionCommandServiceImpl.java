package com.levelup.journey.platform.social.application.internal.commandservices;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.commands.CreateReactionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionCommand;
import com.levelup.journey.platform.social.domain.model.repositories.ReactionRepository;
import com.levelup.journey.platform.social.domain.services.ReactionCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Reaction Command Service Implementation
 * Handles commands for Reaction aggregate
 */
@Service
public class ReactionCommandServiceImpl implements ReactionCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ReactionCommandServiceImpl.class);

    private final ReactionRepository reactionRepository;

    public ReactionCommandServiceImpl(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public Optional<Reaction> handle(CreateReactionCommand command) {
        logger.info("Processing CreateReactionCommand (toggle) for postId: {}, userId: {}, type: {}",
                command.postId(), command.userId(), command.reactionType());

        try {
            // Check if user has already reacted to this post
            var existingReaction = reactionRepository.findByPostIdAndUserId(command.postId(), command.userId());

            if (existingReaction.isPresent()) {
                // TOGGLE: User already reacted, remove the existing reaction
                logger.info("User {} already has a reaction on post {}. Removing existing reaction with ID: {}",
                        command.userId(), command.postId(), existingReaction.get().id());

                reactionRepository.deleteById(existingReaction.get().id());
                logger.info("Reaction removed successfully (toggle) with ID: {}", existingReaction.get().id());

                // Return empty to indicate removal
                return Optional.empty();
            }

            // CREATE: User hasn't reacted, create new reaction
            Reaction reaction = Reaction.create(
                    command.postId(),
                    command.userId(),
                    command.reactionType()
            );

            // Save reaction
            Reaction savedReaction = reactionRepository.save(reaction);
            logger.info("Reaction created and saved successfully with ID: {}", savedReaction.id());

            return Optional.of(savedReaction);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in CreateReactionCommand for postId: {} and userId: {} - {}",
                    command.postId(), command.userId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing CreateReactionCommand for postId: {} and userId: {}",
                    command.postId(), command.userId(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(RemoveReactionCommand command) {
        logger.info("Processing RemoveReactionCommand for reaction ID: {}", command.id());

        try {
            // Check if reaction exists
            var existingReaction = reactionRepository.findById(command.id());
            if (existingReaction.isEmpty()) {
                logger.warn("Attempted to remove non-existent reaction with ID: {}", command.id());
                return false;
            }

            // Delete reaction
            reactionRepository.deleteById(command.id());
            logger.info("Reaction removed successfully with ID: {}", command.id());

            return true;

        } catch (Exception e) {
            logger.error("Unexpected error processing RemoveReactionCommand for ID: {}", command.id(), e);
            return false;
        }
    }
}
