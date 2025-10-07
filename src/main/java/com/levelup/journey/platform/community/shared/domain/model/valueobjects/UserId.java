package com.levelup.journey.platform.community.shared.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a User ID from external IAM context.
 * This is a reference to a user managed by the IAM/Auth microservice.
 */
@Embeddable
public record UserId(Long userId) {
    public UserId {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}
