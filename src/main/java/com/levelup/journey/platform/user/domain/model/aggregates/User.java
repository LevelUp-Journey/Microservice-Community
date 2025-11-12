package com.levelup.journey.platform.user.domain.model.aggregates;

import com.levelup.journey.platform.shared.domain.AggregateRoot;
import com.levelup.journey.platform.user.domain.model.events.UserProfileUpdated;
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
    private final String username;
    private final String profileUrl;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(UserId userId, ProfileId profileId, String username, String profileUrl, Instant createdAt, Instant updatedAt) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.profileId = Objects.requireNonNull(profileId, "Profile ID cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.username = username;
        this.profileUrl = profileUrl;
        this.updatedAt = Objects.requireNonNullElse(updatedAt, createdAt);
    }

    /**
     * Factory method to register a new user (emits event)
     *
     * @param userId the user ID from the authentication system
     * @param profileId the profile ID from the profile bounded context
     * @param username username reported by the Profile bounded context
     * @param profileUrl avatar/profile URL to cache locally
     * @param occurredOn the timestamp when the registration occurred
     * @return a new User instance
     */
    public static User register(UserId userId, ProfileId profileId, String username, String profileUrl, Instant occurredOn) {
        User user = new User(userId, profileId, username, profileUrl, occurredOn, occurredOn);
        user.recordEvent(new UserRegistered(userId, profileId, username, profileUrl, occurredOn));
        return user;
    }

    /**
     * Factory method to restore a user from persistence (no events)
     *
     * @param userId the user ID
     * @param profileId the profile ID
     * @param username cached username (may be null for legacy records)
     * @param profileUrl cached profile URL (may be null for legacy records)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     * @return a restored User instance
     */
    public static User restore(UserId userId, ProfileId profileId, String username, String profileUrl, Instant createdAt, Instant updatedAt) {
        return new User(userId, profileId, username, profileUrl, createdAt, updatedAt != null ? updatedAt : createdAt);
    }

    /**
     * Creates a new instance that contains the latest profile snapshot.
     * @param newUsername username received from the profile microservice
     * @param newProfileUrl profile picture URL received from the profile microservice
     * @param occurredOn timestamp of the update
     * @return user instance with the latest cached data and recorded domain event
     */
    public User updateProfile(String newUsername, String newProfileUrl, Instant occurredOn) {
        String nextUsername = (newUsername == null || newUsername.isBlank()) ? this.username : newUsername;
        String nextProfileUrl = (newProfileUrl == null || newProfileUrl.isBlank()) ? this.profileUrl : newProfileUrl;
        Instant nextUpdatedAt = occurredOn != null ? occurredOn : Instant.now();

        User updatedUser = new User(this.userId, this.profileId, nextUsername, nextProfileUrl, this.createdAt, nextUpdatedAt);
        updatedUser.recordEvent(new UserProfileUpdated(this.userId, this.profileId, nextUsername, nextProfileUrl, nextUpdatedAt));
        return updatedUser;
    }

    // Getters
    public UserId getUserId() {
        return userId;
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
