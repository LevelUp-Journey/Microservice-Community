package com.levelup.journey.platform.post.domain.model.aggregates;

import com.levelup.journey.platform.post.domain.model.events.CommunityCreated;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Community Aggregate: root responsible for encapsulating basic rules of a community.
 */
public final class Community extends AggregateRoot {
    private final CommunityId id;
    private final UserId ownerId;
    private final ProfileId ownerProfileId;
    private String name;
    private String description;
    private ImageUrl imageUrl;
    private final Instant createdAt;

    private Community(CommunityId id, UserId ownerId, ProfileId ownerProfileId, String name, String description, ImageUrl imageUrl, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId required");
        this.ownerProfileId = Objects.requireNonNull(ownerProfileId, "ownerProfileId required");
        this.name = requireText(name, "name");
        this.description = description;
        this.imageUrl = imageUrl != null ? imageUrl : ImageUrl.empty();
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public static Community create(CommunityId id, UserId ownerId, ProfileId ownerProfileId, String name, String description, ImageUrl imageUrl) {
        Community c = new Community(id, ownerId, ownerProfileId, name, description, imageUrl, Instant.now());
        c.recordEvent(new CommunityCreated(id.value(), name, ownerId.value(), Instant.now()));
        return c;
    }

    /** Restore aggregate from persistence (without events). */
    public static Community restore(CommunityId id, UserId ownerId, ProfileId ownerProfileId, String name, String description, ImageUrl imageUrl, Instant createdAt) {
        return new Community(id, ownerId, ownerProfileId, name, description, imageUrl, createdAt);
    }

    public void rename(String newName) {
        this.name = requireText(newName, "newName");
        // In the future, a CommunityRenamed event could be emitted
    }

    /**
     * Update community information
     * @param newName the new name (required)
     * @param newDescription the new description (optional)
     * @param newImageUrl the new image URL (optional)
     */
    public void update(String newName, String newDescription, ImageUrl newImageUrl) {
        this.name = requireText(newName, "newName");
        this.description = newDescription;
        this.imageUrl = newImageUrl != null ? newImageUrl : ImageUrl.empty();
        // In the future, a CommunityUpdated event could be emitted
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required");
        return value;
    }

    public CommunityId id() { return id; }
    public UserId ownerId() { return ownerId; }
    public ProfileId ownerProfileId() { return ownerProfileId; }
    public String name() { return name; }
    public String description() { return description; }
    public ImageUrl imageUrl() { return imageUrl; }
    public Instant createdAt() { return createdAt; }
}
