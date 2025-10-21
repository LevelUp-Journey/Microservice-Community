package com.levelup.journey.platform.domain.model.entities;

import com.levelup.journey.platform.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;

/** Entidad Comentario dentro del agregado Post */
public final class Comment {
    private final CommentId id;
    private final UserId authorId;
    private final String content;
    private final Instant createdAt;

    public Comment(CommentId id, UserId authorId, String content, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id requerido");
        this.authorId = Objects.requireNonNull(authorId, "authorId requerido");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content requerido");
        this.content = content;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public CommentId id() { return id; }
    public UserId authorId() { return authorId; }
    public String content() { return content; }
    public Instant createdAt() { return createdAt; }
}

