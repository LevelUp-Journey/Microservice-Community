package com.levelup.journey.platform.domain.model.events;

import java.time.Instant;

/** Evento emitido cuando se agrega un comentario a un post */
public record CommentAdded(String aggregateId, String commentId, String authorId, Instant occurredOn)
        implements DomainEvent {
    public CommentAdded {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId requerido");
        if (commentId == null || commentId.isBlank()) throw new IllegalArgumentException("commentId requerido");
        if (authorId == null || authorId.isBlank()) throw new IllegalArgumentException("authorId requerido");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn requerido");
    }
    @Override public String eventType() { return getClass().getSimpleName(); }
}

