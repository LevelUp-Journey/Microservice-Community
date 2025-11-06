package com.levelup.journey.platform.post.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/** Identificador de Comunidad */
public record CommunityId(String value) {
    public CommunityId {
        Objects.requireNonNull(value, "CommunityId no puede ser null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CommunityId no puede estar vacío");
        }
        validateUUID(value);
    }
    
    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("CommunityId debe ser un UUID válido: " + value, e);
        }
    }
    
    public static CommunityId of(String value) { return new CommunityId(value); }
    public static CommunityId random() { return new CommunityId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value; }
}
