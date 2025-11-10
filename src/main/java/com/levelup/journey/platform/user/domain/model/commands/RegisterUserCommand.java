package com.levelup.journey.platform.user.domain.model.commands;

import java.time.Instant;

/**
 * Command to register a new user in the system.
 * This command is typically created when receiving a user registration event from Kafka.
 *
 * @param userId the user ID from the authentication system
 * @param profileId the profile ID from the profile bounded context
 * @param occurredOn the timestamp when the registration occurred
 */
public record RegisterUserCommand(
    String userId,
    String profileId,
    Instant occurredOn
) {
    public RegisterUserCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("profileId cannot be null or empty");
        }
        if (occurredOn == null) {
            throw new IllegalArgumentException("occurredOn cannot be null");
        }
    }
}
