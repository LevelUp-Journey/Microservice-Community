package com.levelup.journey.platform.user.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain event representing that a user has been registered in the system.
 * This event is triggered when a user registration event is received from Kafka.
 */
@Getter
public class UserRegistered implements DomainEvent {
    private final UserId userId;
    private final ProfileId profileId;
    private final Instant occurredOn;

    public UserRegistered(UserId userId, ProfileId profileId, Instant occurredOn) {
        this.userId = userId;
        this.profileId = profileId;
        this.occurredOn = occurredOn;
    }

    @Override
    public String aggregateId() {
        return userId.value();
    }

    @Override
    public String eventType() {
        return "UserRegistered";
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
