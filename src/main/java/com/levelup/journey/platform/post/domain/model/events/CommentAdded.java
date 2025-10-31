package com.levelup.journey.platform.post.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/** Event emitted when a comment is added to a post */
public class CommentAdded implements DomainEvent {
    private final String aggregateId;
    private final String commentId;
    private final String authorId;
    private final Instant occurredOn;

    public CommentAdded(String aggregateId, String commentId, String authorId, Instant occurredOn) {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (commentId == null || commentId.isBlank()) throw new IllegalArgumentException("commentId required");
        if (authorId == null || authorId.isBlank()) throw new IllegalArgumentException("authorId required");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn required");
        this.aggregateId = aggregateId;
        this.commentId = commentId;
        this.authorId = authorId;
        this.occurredOn = occurredOn;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public String eventType() {
        return getClass().getSimpleName();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentAdded that = (CommentAdded) o;

        if (!aggregateId.equals(that.aggregateId)) return false;
        if (!commentId.equals(that.commentId)) return false;
        if (!authorId.equals(that.authorId)) return false;
        return occurredOn.equals(that.occurredOn);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + commentId.hashCode();
        result = 31 * result + authorId.hashCode();
        result = 31 * result + occurredOn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CommentAdded{" +
                "aggregateId='" + aggregateId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}

