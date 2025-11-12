package com.levelup.journey.platform.shared.domain.acl;

/**
 * Anti-Corruption Layer interface for community services.
 * This interface defines the contract for cross-context communication
 * to access community data from other bounded contexts.
 */
public interface CommunityService {

    /**
     * Gets the name of a community by its ID.
     *
     * @param communityId The ID of the community
     * @return The name of the community, or null if not found
     */
    String getCommunityName(String communityId);

    /**
     * Gets the image URL of a community by its ID.
     *
     * @param communityId The ID of the community
     * @return The image URL of the community, or null if not found
     */
    String getCommunityImageUrl(String communityId);
}