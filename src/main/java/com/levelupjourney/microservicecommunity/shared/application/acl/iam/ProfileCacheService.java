package com.levelupjourney.microservicecommunity.shared.application.acl.iam;

import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Service for managing cached user profiles from the IAM service.
 * This service acts as a local cache to avoid frequent calls to the external IAM service.
 */
public interface ProfileCacheService {
    
    /**
     * Gets a cached profile, fetching from IAM if not present or stale.
     * 
     * @param userId the user identifier
     * @return the profile snapshot if found
     */
    Optional<ProfileSnapshot> getProfile(UserId userId);
    
    /**
     * Refreshes a profile in the cache by fetching from IAM.
     * 
     * @param userId the user identifier
     * @return the updated profile snapshot if found
     */
    Optional<ProfileSnapshot> refreshProfile(UserId userId);
    
    /**
     * Removes a profile from the cache.
     * 
     * @param userId the user identifier
     */
    void evictProfile(UserId userId);
    
    /**
     * Validates if a user exists, checking cache first then IAM.
     * 
     * @param userId the user identifier
     * @return true if the user exists
     */
    boolean validateUser(UserId userId);
}
