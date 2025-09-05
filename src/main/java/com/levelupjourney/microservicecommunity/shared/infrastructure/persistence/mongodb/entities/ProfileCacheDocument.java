package com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.entities;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB document for caching user profiles from the external IAM service.
 * This entity is part of the ACL infrastructure.
 */
@Getter
@Document(collection = "profile_cache")
public class ProfileCacheDocument {
    
    @Id
    private String id; // This will be the userId
    
    @Field("username")
    private String username;
    
    @Field("name")
    private String name;
    
    @Field("avatar_url")
    private String avatarUrl;
    
    @Field("roles")
    private List<String> roles;
    
    @Field("last_synced_at")
    private LocalDateTime lastSyncedAt;
    
    public ProfileCacheDocument() {}
    
    public ProfileCacheDocument(
        String id,
        String username,
        String name,
        String avatarUrl,
        List<String> roles,
        LocalDateTime lastSyncedAt
    ) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.lastSyncedAt = lastSyncedAt;
    }
    
    /**
     * Updates the profile data and sync timestamp.
     */
    public void updateProfile(
        String username,
        String name,
        String avatarUrl,
        List<String> roles
    ) {
        this.username = username;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.lastSyncedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the cached profile is stale (older than specified minutes).
     * 
     * @param staleAfterMinutes minutes after which the profile is considered stale
     * @return true if the profile is stale
     */
    public boolean isStale(int staleAfterMinutes) {
        return lastSyncedAt.isBefore(LocalDateTime.now().minusMinutes(staleAfterMinutes));
    }
}
