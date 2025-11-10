package com.levelup.journey.platform.user.infrastructure.messaging.kafka.listeners;

import com.levelup.journey.platform.user.domain.model.commands.RegisterUserCommand;
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
 * Kafka listener for user registration events
 * Listens to the user.registered topic and processes incoming events
 */
@Component
public class UserRegistrationKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationKafkaListener.class);
    private final UserCommandService userCommandService;

    public UserRegistrationKafkaListener(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    /**
     * Listens to user registration events from Kafka
     *
     * @param event the user registration event
     * @param partition the Kafka partition
     * @param offset the message offset
     */
    @KafkaListener(
            topics = "${kafka.topics.user-registered:user.registered}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserRegistration(
            @Payload UserRegistrationKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("Received user registration event from Kafka - userId: {}, profileId: {}, partition: {}, offset: {}",
                event.getUserId(), event.getProfileId(), partition, offset);

        try {
            // Create command from Kafka event
            RegisterUserCommand command = new RegisterUserCommand(
                    event.getUserId(),
                    event.getProfileId(),
                    event.getOccurredOnAsInstant()
            );

            // Handle the command
            var result = userCommandService.handle(command);

            if (result.isPresent()) {
                logger.info("Successfully registered user from Kafka event: userId={}", event.getUserId());
            } else {
                logger.warn("Failed to register user from Kafka event: userId={}", event.getUserId());
            }

        } catch (Exception e) {
            logger.error("Error processing user registration event: userId={}, error={}",
                    event.getUserId(), e.getMessage(), e);
            // Consider implementing a dead letter queue or retry mechanism here
        }
    }
}
