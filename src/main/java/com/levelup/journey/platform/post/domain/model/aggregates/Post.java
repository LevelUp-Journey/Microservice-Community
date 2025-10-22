package com.levelup.journey.platform.post.domain.model.aggregates;

import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.events.PostPublished;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Post Aggregate
 * Note: Comments are stored in a separate table and loaded on-demand.
 * The comments list is transient and only populated when explicitly loaded.
 */
public final class Post extends AggregateRoot {
    private final PostId id;
    private final CommunityId communityId;
    private final UserId authorId;
    private String title;
    private String content;
    private final Instant createdAt;
    private final List<Comment> comments = new ArrayList<>();

    private Post(PostId id, CommunityId communityId, UserId authorId, String title, String content, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.communityId = Objects.requireNonNull(communityId, "communityId required");
        this.authorId = Objects.requireNonNull(authorId, "authorId required");
        this.title = requireText(title, "title");
        this.content = requireText(content, "content");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public static Post publish(PostId id, CommunityId communityId, UserId authorId, String title, String content) {
        Post p = new Post(id, communityId, authorId, title, content, Instant.now());
        p.recordEvent(new PostPublished(id.value(), communityId.value(), authorId.value(), title, content, Instant.now()));
        return p;
    }

    /**
     * Restore aggregate from persistence (without events).
     * @param id post identifier
     * @param communityId community identifier
     * @param authorId author identifier
     * @param title post title
     * @param content post content
     * @param createdAt creation timestamp
     * @param existingComments optional list of comments (can be empty if not loaded)
     * @return restored Post aggregate
     */
    public static Post restore(PostId id, CommunityId communityId, UserId authorId, String title, String content, Instant createdAt, List<Comment> existingComments) {
        Post p = new Post(id, communityId, authorId, title, content, createdAt);
        if (existingComments != null && !existingComments.isEmpty()) {
            p.comments.addAll(existingComments);
        }
        return p;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required");
        return value;
    }

    public PostId id() { return id; }
    public CommunityId communityId() { return communityId; }
    public UserId authorId() { return authorId; }
    public String title() { return title; }
    public String content() { return content; }
    public Instant createdAt() { return createdAt; }

    /**
     * Returns the comments collection.
     * Note: This may be empty if comments were not explicitly loaded.
     * Use CommentQueryService to load comments when needed.
     */
    public List<Comment> comments() { return Collections.unmodifiableList(comments); }
}
