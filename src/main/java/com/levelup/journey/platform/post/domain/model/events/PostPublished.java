
package com.levelup.journey.platform.post.domain.model.events;

import java.time.Instant;

/** Evento emitido cuando se publica un post */
public record PostPublished(String aggregateId, String communityId, String authorId, String title, Instant occurredOn)
        implements DomainEvent {
    public PostPublished {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId requerido");
        if (communityId == null || communityId.isBlank()) throw new IllegalArgumentException("communityId requerido");
        if (authorId == null || authorId.isBlank()) throw new IllegalArgumentException("authorId requerido");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title requerido");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn requerido");
    }
    @Override public String eventType() { return getClass().getSimpleName(); }
}

