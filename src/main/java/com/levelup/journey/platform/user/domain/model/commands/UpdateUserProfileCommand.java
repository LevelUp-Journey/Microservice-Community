package com.levelup.journey.platform.user.domain.model.commands;

import java.time.Instant;

/**
 * Command that updates cached profile data for a user whenever the profile microservice emits a change.
 *
 * @param userId unique identifier of the user in the auth system
 * @param profileId identifier of the profile coming from the Profile bounded context
 * @param username public username to store locally
 * @param profileUrl avatar/profile URL to store locally
 * @param occurredOn timestamp when the profile update happened
 */
public record UpdateUserProfileCommand(
        String userId,
        String profileId,
        String username,
        String profileUrl,
        Instant occurredOn
) {
    public UpdateUserProfileCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("profileId cannot be null or empty");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (profileUrl == null || profileUrl.isBlank()) {
            throw new IllegalArgumentException("profileUrl cannot be null or empty");
        }
        if (occurredOn == null) {
            throw new IllegalArgumentException("occurredOn cannot be null");
        }
    }
}
