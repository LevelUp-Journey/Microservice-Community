package com.levelup.journey.platform.post.domain.model.entities;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;

/** Comment Entity within the Post Aggregate */
public final class Comment {
    private final CommentId id;
    private final UserId authorId;
    private final String content;
    private final Instant createdAt;

    public Comment(CommentId id, UserId authorId, String content, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.authorId = Objects.requireNonNull(authorId, "authorId required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
        this.content = content;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public CommentId id() { return id; }
    public UserId authorId() { return authorId; }
    public String content() { return content; }
    public Instant createdAt() { return createdAt; }
}

