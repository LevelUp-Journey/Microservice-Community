package com.levelup.journey.platform.post.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/** Identificador de Post */
public record PostId(String value) {
    public PostId {
        Objects.requireNonNull(value, "PostId no puede ser null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("PostId no puede estar vacío");
        }
        validateUUID(value);
    }
    
    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PostId debe ser un UUID válido: " + value, e);
        }
    }
    
    public static PostId of(String value) { return new PostId(value); }
    public static PostId random() { return new PostId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value; }
}

