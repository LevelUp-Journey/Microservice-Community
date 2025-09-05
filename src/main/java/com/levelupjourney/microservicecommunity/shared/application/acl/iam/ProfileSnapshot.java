package com.levelupjourney.microservicecommunity.shared.application.acl.iam;

import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Profile information cached from the external IAM service.
 * This represents the cached snapshot of user profile data.
 */
public record ProfileSnapshot(
    UserId userId,
    String username,
    String name,
    String avatarUrl,
    List<String> roles,
    LocalDateTime lastSyncedAt
) {
    
    public ProfileSnapshot {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (roles == null) {
            throw new IllegalArgumentException("Roles cannot be null");
        }
        if (lastSyncedAt == null) {
            throw new IllegalArgumentException("LastSyncedAt cannot be null");
        }
    }
    
    /**
     * Creates a profile snapshot for embedding in read models.
     * 
     * @return an author snapshot for read models
     */
    public AuthorSnapshot toAuthorSnapshot() {
        return new AuthorSnapshot(
            userId.value(),
            username,
            name,
            avatarUrl,
            roles
        );
    }
}
