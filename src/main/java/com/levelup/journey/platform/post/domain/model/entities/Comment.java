package com.levelup.journey.platform.post.domain.model.entities;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;

import java.time.Instant;
import java.util.Objects;

/** Comment Entity within the Post Aggregate */
public final class Comment {
    private final CommentId id;
    private final UserId authorId;
    private final ProfileId authorProfileId;
    private final String content;
    private final ImageUrl imageUrl;
    private final Instant createdAt;

    public Comment(CommentId id, UserId authorId, ProfileId authorProfileId, String content, ImageUrl imageUrl, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.authorId = Objects.requireNonNull(authorId, "authorId required");
        this.authorProfileId = Objects.requireNonNull(authorProfileId, "authorProfileId required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
        this.content = content;
        this.imageUrl = imageUrl != null ? imageUrl : ImageUrl.empty();
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public CommentId id() { return id; }
    public UserId authorId() { return authorId; }
    public ProfileId authorProfileId() { return authorProfileId; }
    public String content() { return content; }
    public ImageUrl imageUrl() { return imageUrl; }
    public Instant createdAt() { return createdAt; }
}

