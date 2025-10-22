package com.levelup.journey.platform.social.application.internal.commandservices;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.commands.CreateSubscriptionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveSubscriptionCommand;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.services.SubscriptionCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Subscription Command Service Implementation
 * Handles commands for Subscription aggregate
 */
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionCommandServiceImpl.class);

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> handle(CreateSubscriptionCommand command) {
        logger.info("Processing CreateSubscriptionCommand for subscription ID: {}, userId: {}, communityId: {}",
                command.id(), command.userId(), command.communityId());

        try {
            // Check if subscription with this ID already exists
            var existingSubscription = subscriptionRepository.findById(command.id());
            if (existingSubscription.isPresent()) {
                logger.warn("Attempted to create subscription with existing ID: {}", command.id());
                throw new IllegalArgumentException("Ya existe una suscripción con el ID: " + command.id());
            }

            // Check if user is already subscribed to this community
            var userSubscription = subscriptionRepository.findByUserIdAndCommunityId(
                    command.userId(),
                    command.communityId()
            );
            if (userSubscription.isPresent()) {
                logger.warn("User {} is already subscribed to community {}",
                        command.userId(), command.communityId());
                throw new IllegalArgumentException("El usuario ya está suscrito a esta comunidad");
            }

            // Create new subscription
            Subscription subscription = Subscription.create(
                    command.id(),
                    command.userId(),
                    command.communityId()
            );

            // Save subscription
            Subscription savedSubscription = subscriptionRepository.save(subscription);
            logger.info("Subscription created and saved successfully with ID: {}", savedSubscription.id());

            return Optional.of(savedSubscription);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in CreateSubscriptionCommand for ID: {} - {}", command.id(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing CreateSubscriptionCommand for ID: {}", command.id(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(RemoveSubscriptionCommand command) {
        logger.info("Processing RemoveSubscriptionCommand for subscription ID: {}", command.id());

        try {
            // Check if subscription exists
            var existingSubscription = subscriptionRepository.findById(command.id());
            if (existingSubscription.isEmpty()) {
                logger.warn("Attempted to remove non-existent subscription with ID: {}", command.id());
                return false;
            }

            // Delete subscription
            subscriptionRepository.deleteById(command.id());
            logger.info("Subscription removed successfully with ID: {}", command.id());

            return true;

        } catch (Exception e) {
            logger.error("Unexpected error processing RemoveSubscriptionCommand for ID: {}", command.id(), e);
            return false;
        }
    }
}
