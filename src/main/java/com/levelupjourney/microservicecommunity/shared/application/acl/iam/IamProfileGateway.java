package com.levelupjourney.microservicecommunity.shared.application.acl.iam;

import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Gateway interface for accessing external IAM service.
 * This is the ACL interface that abstracts external IAM communication.
 * 
 * The implementation will handle the actual HTTP calls to the IAM service,
 * while this interface keeps the domain clean from external dependencies.
 */
public interface IamProfileGateway {
    
    /**
     * Fetches a user profile from the external IAM service.
     * 
     * @param userId the user identifier
     * @return the profile snapshot if found, empty otherwise
     */
    Optional<ProfileSnapshot> fetchProfile(UserId userId);
    
    /**
     * Validates if a user exists in the IAM service.
     * 
     * @param userId the user identifier
     * @return true if the user exists, false otherwise
     */
    boolean userExists(UserId userId);
}
