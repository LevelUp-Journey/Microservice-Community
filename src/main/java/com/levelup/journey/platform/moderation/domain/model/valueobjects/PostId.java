package com.levelup.journey.platform.moderation.domain.model.valueobjects;

import java.util.UUID;

public record PostId(UUID value) {
    public PostId {
        if (value == null) {
            throw new IllegalArgumentException("PostId cannot be null");
        }
    }
    
    public static PostId of(UUID value) {
        return new PostId(value);
    }
}