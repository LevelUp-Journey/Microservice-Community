package com.levelup.journey.platform.post.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/** Identificador de Usuario */
public record UserId(String value) {
    public UserId {
        Objects.requireNonNull(value, "UserId no puede ser null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("UserId no puede estar vacío");
        }
        validateUUID(value);
    }
    
    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("UserId debe ser un UUID válido: " + value, e);
        }
    }
    
    public static UserId of(String value) { return new UserId(value); }
    public static UserId random() { return new UserId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value; }
}
