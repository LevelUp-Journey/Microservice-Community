package com.levelup.journey.platform.post.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/** Identificador de Comentario */
public record CommentId(String value) {
    public CommentId {
        Objects.requireNonNull(value, "CommentId no puede ser null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CommentId no puede estar vacío");
        }
        validateUUID(value);
    }
    
    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("CommentId debe ser un UUID válido: " + value, e);
        }
    }
    
    public static CommentId of(String value) { return new CommentId(value); }
    public static CommentId random() { return new CommentId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value; }
}

