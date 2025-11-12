
package com.levelup.journey.platform.post.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/** Evento emitido cuando se publica un post */
public class PostPublished implements DomainEvent {
    private final String aggregateId;
    private final String communityId;
    private final String authorId;
    private final String content;
    private final Instant occurredOn;

    public PostPublished(String aggregateId, String communityId, String authorId, String content, Instant occurredOn) {
        this.aggregateId = aggregateId;
        this.communityId = communityId;
        this.authorId = authorId;
        this.content = content;
        this.occurredOn = occurredOn;

        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId requerido");
        if (communityId == null || communityId.isBlank()) throw new IllegalArgumentException("communityId requerido");
        if (authorId == null || authorId.isBlank()) throw new IllegalArgumentException("authorId requerido");
        if (content == null) throw new IllegalArgumentException("content requerido");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn requerido");
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

    public String communityId() {
        return communityId;
    }

    public String authorId() {
        return authorId;
    }

    public String content() {
        return content;
    }
}

