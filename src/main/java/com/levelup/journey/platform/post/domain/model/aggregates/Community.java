package com.levelup.journey.platform.post.domain.model.aggregates;

import com.levelup.journey.platform.post.domain.model.events.CommunityCreated;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Community Aggregate: root responsible for encapsulating basic rules of a community.
 */
public final class Community extends AggregateRoot {
    private final CommunityId id;
    private final UserId ownerId;
    private String name;
    private String description;
    private final Instant createdAt;

    private Community(CommunityId id, UserId ownerId, String name, String description, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId required");
        this.name = requireText(name, "name");
        this.description = description;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public static Community create(CommunityId id, UserId ownerId, String name, String description) {
        Community c = new Community(id, ownerId, name, description, Instant.now());
        c.recordEvent(new CommunityCreated(id.value(), name, ownerId.value(), Instant.now()));
        return c;
    }

    /** Restore aggregate from persistence (without events). */
    public static Community restore(CommunityId id, UserId ownerId, String name, String description, Instant createdAt) {
        return new Community(id, ownerId, name, description, createdAt);
    }

    public void rename(String newName) {
        this.name = requireText(newName, "newName");
        // In the future, a CommunityRenamed event could be emitted
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required");
        return value;
    }

    public CommunityId id() { return id; }
    public UserId ownerId() { return ownerId; }
    public String name() { return name; }
    public String description() { return description; }
    public Instant createdAt() { return createdAt; }
}
