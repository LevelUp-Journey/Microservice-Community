package com.levelupjourney.microservicecommunity.shared.application.acl.iam;

import java.util.List;

/**
 * Author snapshot for embedding in read models.
 * This is a simplified version of ProfileSnapshot specifically for read models.
 */
public record AuthorSnapshot(
    String id,
    String username,
    String name,
    String avatarUrl,
    List<String> roles
) {
    
    public AuthorSnapshot {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Id cannot be null or empty");
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
    }
}
