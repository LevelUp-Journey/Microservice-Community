package com.levelup.journey.platform.shared.domain.acl;

import java.util.List;

/**
 * Anti-Corruption Layer interface for social relationship services.
 * This interface defines the contract for cross-context communication
 * between the Post context and the Social context.
 */
public interface SocialRelationshipService {

    /**
     * Checks if a user is following another user.
     *
     * @param followerId The ID of the potential follower
     * @param followingId The ID of the user being followed
     * @return true if the follower relationship exists
     */
    boolean isFollowing(String followerId, String followingId);

    /**
     * Gets all users that a given user is following.
     *
     * @param userId The ID of the user
     * @return List of user IDs that this user follows
     */
    List<String> getFollowing(String userId);

    /**
     * Checks if a user is subscribed to a community.
     *
     * @param userId The ID of the user
     * @param communityId The ID of the community
     * @return true if the subscription exists
     */
    boolean isSubscribedToCommunity(String userId, String communityId);

    /**
     * Gets all communities that a user is subscribed to.
     *
     * @param userId The ID of the user
     * @return List of community IDs that this user is subscribed to
     */
    List<String> getSubscribedCommunities(String userId);
}