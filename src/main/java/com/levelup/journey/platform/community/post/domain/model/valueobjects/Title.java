package com.levelup.journey.platform.community.post.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

/**
 * Value object representing a post title.
 * Ensures title is valid and not empty.
 */
@Embeddable
public record Title(
        @NotBlank(message = "Title cannot be blank")
        String value
) {
    public Title {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Title cannot exceed 255 characters");
        }
    }
}
