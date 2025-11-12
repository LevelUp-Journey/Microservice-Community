package com.levelup.journey.platform.user.infrastructure.messaging.kafka.listeners;

import com.levelup.journey.platform.user.domain.model.commands.UpdateUserProfileCommand;
import com.levelup.journey.platform.user.domain.services.UserCommandService;
import com.levelup.journey.platform.user.infrastructure.messaging.kafka.events.UserRegistrationKafkaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that keeps cached user data in sync with the Profile microservice.
 */
@Component
public class UserProfileUpdatedKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileUpdatedKafkaListener.class);
    private final UserCommandService userCommandService;

    public UserProfileUpdatedKafkaListener(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @KafkaListener(
            topics = "${kafka.topics.profile-updated:community.profile.updated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleProfileUpdated(
            @Payload UserRegistrationKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("Received profile update event from Kafka - userId: {}, profileId: {}, username: {}, partition: {}, offset: {}",
                event.getUserId(), event.getProfileId(), event.getUsername(), partition, offset);

        try {
            UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                    event.getUserId(),
                    event.getProfileId(),
                    event.getUsername(),
                    event.getProfileUrl(),
                    event.getOccurredOnAsInstant()
            );

            var result = userCommandService.handle(command);
            if (result.isPresent()) {
                logger.info("Updated cached profile data successfully for userId={}", event.getUserId());
            } else {
                logger.warn("Profile update ignored because user was not found: userId={}, profileId={}",
                        event.getUserId(), event.getProfileId());
            }
        } catch (Exception e) {
            logger.error("Error processing profile update event: userId={}, error={}",
                    event.getUserId(), e.getMessage(), e);
        }
    }
}
