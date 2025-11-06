package com.levelup.journey.platform.social.interfaces.rest.resources;

import java.util.List;

/**
 * Feed Sources Resource
 * Response payload containing sources for a user's feed
 * Includes user IDs being followed and community IDs subscribed to
 */
public record FeedSourcesResource(
        String userId,
        List<String> followedUserIds,
        List<String> subscribedCommunityIds,
        int totalSources
) {
}
