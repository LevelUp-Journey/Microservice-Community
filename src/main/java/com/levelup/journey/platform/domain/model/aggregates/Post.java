package com.levelup.journey.platform.domain.model.aggregates;

import com.levelup.journey.platform.domain.model.entities.Comment;
import com.levelup.journey.platform.domain.model.events.CommentAdded;
import com.levelup.journey.platform.domain.model.events.PostPublished;
import com.levelup.journey.platform.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Post Aggregate */
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
        p.recordEvent(new PostPublished(id.value(), communityId.value(), authorId.value(), title, Instant.now()));
        return p;
    }

    /** Restore aggregate from persistence (without events). */
    public static Post restore(PostId id, CommunityId communityId, UserId authorId, String title, String content, Instant createdAt, List<Comment> existingComments) {
        Post p = new Post(id, communityId, authorId, title, content, createdAt);
        if (existingComments != null && !existingComments.isEmpty()) {
            p.comments.addAll(existingComments);
        }
        return p;
    }

    public Comment addComment(CommentId commentId, UserId authorId, String content) {
        Comment c = new Comment(commentId, authorId, requireText(content, "content"), Instant.now());
        this.comments.add(c);
        this.recordEvent(new CommentAdded(this.id.value(), commentId.value(), authorId.value(), Instant.now()));
        return c;
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
    public List<Comment> comments() { return Collections.unmodifiableList(comments); }
}
