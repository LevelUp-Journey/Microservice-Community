package com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.repositories;

import com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.entities.ProfileCacheDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for managing cached user profiles.
 * Part of the ACL infrastructure for IAM integration.
 */
@Repository
public interface ProfileCacheRepository extends MongoRepository<ProfileCacheDocument, String> {
    
    /**
     * Checks if a profile exists in the cache.
     * 
     * @param userId the user identifier
     * @return true if the profile exists
     */
    boolean existsById(String userId);
}
