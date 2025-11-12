package com.levelup.journey.platform.user.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain event emitted whenever cached profile data changes.
 */
@Getter
public class UserProfileUpdated implements DomainEvent {
    private final UserId userId;
    private final ProfileId profileId;
    private final String username;
    private final String profileUrl;
    private final Instant occurredOn;

    public UserProfileUpdated(UserId userId, ProfileId profileId, String username, String profileUrl, Instant occurredOn) {
        this.userId = userId;
        this.profileId = profileId;
        this.username = username;
        this.profileUrl = profileUrl;
        this.occurredOn = occurredOn;
    }

    @Override
    public String aggregateId() {
        return userId.value();
    }

    @Override
    public String eventType() {
        return "UserProfileUpdated";
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
