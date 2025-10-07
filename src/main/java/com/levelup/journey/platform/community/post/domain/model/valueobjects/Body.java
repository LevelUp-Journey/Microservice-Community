package com.levelup.journey.platform.community.post.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

/**
 * Value object representing a post body content.
 * Ensures body is valid and not empty.
 */
@Embeddable
public record Body(
        @NotBlank(message = "Body cannot be blank")
        @Column(columnDefinition = "TEXT")
        String value
) {
    public Body {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Body cannot be null or blank");
        }
    }
}
