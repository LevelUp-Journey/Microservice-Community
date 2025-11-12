package com.levelup.journey.platform.post.domain.model.aggregates;

import com.levelup.journey.platform.post.domain.model.events.PostPublished;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Post Aggregate
 */
public final class Post extends AggregateRoot {
    private final PostId id;
    private final CommunityId communityId;
    private final UserId authorId;
    private final ProfileId authorProfileId;
    private String content;
    private ImageUrl imageUrl;
    private final Instant createdAt;

    private Post(PostId id, CommunityId communityId, UserId authorId, ProfileId authorProfileId, String content, ImageUrl imageUrl, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.communityId = Objects.requireNonNull(communityId, "communityId required");
        this.authorId = Objects.requireNonNull(authorId, "authorId required");
        this.authorProfileId = Objects.requireNonNull(authorProfileId, "authorProfileId required");
        this.content = requireText(content, "content");
        this.imageUrl = imageUrl != null ? imageUrl : ImageUrl.empty();
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public static Post publish(PostId id, CommunityId communityId, UserId authorId, ProfileId authorProfileId, String content, ImageUrl imageUrl) {
        Post p = new Post(id, communityId, authorId, authorProfileId, content, imageUrl, Instant.now());
        p.recordEvent(new PostPublished(id.value(), communityId.value(), authorId.value(), content, Instant.now()));
        return p;
    }

    /**
     * Restore aggregate from persistence (without events).
     * @param id post identifier
     * @param communityId community identifier
     * @param authorId author identifier
     * @param authorProfileId author profile identifier
     * @param content post content (supports Markdown)
     * @param imageUrl optional image URL
     * @param createdAt creation timestamp
     * @return restored Post aggregate
     */
    public static Post restore(PostId id, CommunityId communityId, UserId authorId, ProfileId authorProfileId, String content, ImageUrl imageUrl, Instant createdAt) {
        return new Post(id, communityId, authorId, authorProfileId, content, imageUrl, createdAt);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required");
        return value;
    }

    public PostId id() { return id; }
    public CommunityId communityId() { return communityId; }
    public UserId authorId() { return authorId; }
    public ProfileId authorProfileId() { return authorProfileId; }
    public String content() { return content; }
    public ImageUrl imageUrl() { return imageUrl; }
    public Instant createdAt() { return createdAt; }
}