package com.levelup.journey.platform.user.domain.model.aggregates;

import com.levelup.journey.platform.shared.domain.AggregateRoot;
import com.levelup.journey.platform.user.domain.model.events.UserRegistered;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * User aggregate root
 * Represents a user registered in the community service
 * This is a local copy of user data needed for the community bounded context
 */
public final class User extends AggregateRoot {
    private final UserId userId;
    private final ProfileId profileId;
    private final Instant createdAt;

    private User(UserId userId, ProfileId profileId, Instant createdAt) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.profileId = Objects.requireNonNull(profileId, "Profile ID cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    /**
     * Factory method to register a new user (emits event)
     *
     * @param userId the user ID from the authentication system
     * @param profileId the profile ID from the profile bounded context
     * @param occurredOn the timestamp when the registration occurred
     * @return a new User instance
     */
    public static User register(UserId userId, ProfileId profileId, Instant occurredOn) {
        User user = new User(userId, profileId, occurredOn);
        user.recordEvent(new UserRegistered(userId, profileId, occurredOn));
        return user;
    }

    /**
     * Factory method to restore a user from persistence (no events)
     *
     * @param userId the user ID
     * @param profileId the profile ID
     * @param createdAt the creation timestamp
     * @return a restored User instance
     */
    public static User restore(UserId userId, ProfileId profileId, Instant createdAt) {
        return new User(userId, profileId, createdAt);
    }

    // Getters
    public UserId getUserId() {
        return userId;
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
