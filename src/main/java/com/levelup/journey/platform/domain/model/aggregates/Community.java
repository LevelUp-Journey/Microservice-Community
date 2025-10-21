package com.levelup.journey.platform.domain.model.aggregates;

import com.levelup.journey.platform.domain.model.events.CommunityCreated;
import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Agregado Comunidad: raíz encargada de encapsular reglas básicas de una comunidad.
 */
public final class Community extends AggregateRoot {
    private final CommunityId id;
    private final UserId ownerId;
    private String name;
    private String description;
    private final Instant createdAt;

    private Community(CommunityId id, UserId ownerId, String name, String description, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id requerido");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId requerido");
        this.name = requireText(name, "name");
        this.description = description;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public static Community create(CommunityId id, UserId ownerId, String name, String description) {
        Community c = new Community(id, ownerId, name, description, Instant.now());
        c.recordEvent(new CommunityCreated(id.value(), name, ownerId.value(), Instant.now()));
        return c;
    }

    public void rename(String newName) {
        this.name = requireText(newName, "newName");
        // En un futuro se podría emitir un evento CommunityRenamed
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " requerido");
        return value;
    }

    public CommunityId id() { return id; }
    public UserId ownerId() { return ownerId; }
    public String name() { return name; }
    public String description() { return description; }
    public Instant createdAt() { return createdAt; }
}

