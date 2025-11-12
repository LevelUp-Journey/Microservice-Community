package com.levelup.journey.platform.user.interfaces.acl;

/**
 * ACL Facade for User Context
 * Provides access to user data for other bounded contexts
 */
public interface UserContextFacade {

    /**
     * Gets the username for a user by user ID
     * @param userId the user ID
     * @return the username, or null if not found
     */
    String getUsernameByUserId(String userId);

    /**
     * Gets the profile URL for a user by user ID
     * @param userId the user ID
     * @return the profile URL, or null if not found
     */
    String getProfileUrlByUserId(String userId);
}