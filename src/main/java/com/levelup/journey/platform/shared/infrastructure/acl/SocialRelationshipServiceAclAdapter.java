package com.levelup.journey.platform.shared.infrastructure.acl;

import com.levelup.journey.platform.shared.domain.acl.SocialRelationshipService;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowingByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByUserIdQuery;
import com.levelup.journey.platform.social.domain.services.FollowQueryService;
import com.levelup.journey.platform.social.domain.services.SubscriptionQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ACL Adapter for social relationship services.
 * Translates between the shared ACL interface and the Social bounded context.
 */
@Service
public class SocialRelationshipServiceAclAdapter implements SocialRelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(SocialRelationshipServiceAclAdapter.class);

    private final FollowQueryService followQueryService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SocialRelationshipServiceAclAdapter(FollowQueryService followQueryService,
                                             SubscriptionQueryService subscriptionQueryService) {
        this.followQueryService = followQueryService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    @Override
    public boolean isFollowing(String followerId, String followingId) {
        try {
            // Translate to Social context query
            var query = new GetFollowingByUserIdQuery(
                com.levelup.journey.platform.social.domain.model.valueobjects.UserId.of(
                    followerId
                )
            );

            // Execute query
            var following = followQueryService.handle(query);

            // Check if the followingId is in the list
            return following.stream()
                .anyMatch(follow -> follow.followingId().value().toString().equals(followingId));

        } catch (Exception e) {
            logger.error("Error in social relationship ACL adapter checking follow relationship: {}",
                        e.getMessage());
            return false; // Fail-safe: assume not following
        }
    }

    @Override
    public List<String> getFollowing(String userId) {
        try {
            // Translate to Social context query
            var query = new GetFollowingByUserIdQuery(
                com.levelup.journey.platform.social.domain.model.valueobjects.UserId.of(
                    userId
                )
            );

            // Execute query and translate results
            var following = followQueryService.handle(query);
            return following.stream()
                .map(follow -> follow.followingId().value().toString())
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error in social relationship ACL adapter getting following: {}",
                        e.getMessage());
            return List.of(); // Fail-safe: return empty list
        }
    }

    @Override
    public boolean isSubscribedToCommunity(String userId, String communityId) {
        try {
            // Translate to Social context query
            var query = new GetSubscriptionsByUserIdQuery(
                com.levelup.journey.platform.social.domain.model.valueobjects.UserId.of(
                    userId
                )
            );

            // Execute query
            var subscriptions = subscriptionQueryService.handle(query);

            // Check if the communityId is in the list
            return subscriptions.stream()
                .anyMatch(sub -> sub.communityId().value().toString().equals(communityId));

        } catch (Exception e) {
            logger.error("Error in social relationship ACL adapter checking subscription: {}",
                        e.getMessage());
            return false; // Fail-safe: assume not subscribed
        }
    }

    @Override
    public List<String> getSubscribedCommunities(String userId) {
        try {
            // Translate to Social context query
            var query = new GetSubscriptionsByUserIdQuery(
                com.levelup.journey.platform.social.domain.model.valueobjects.UserId.of(
                    userId
                )
            );

            // Execute query and translate results
            var subscriptions = subscriptionQueryService.handle(query);
            return subscriptions.stream()
                .map(sub -> sub.communityId().value().toString())
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error in social relationship ACL adapter getting subscriptions: {}",
                        e.getMessage());
            return List.of(); // Fail-safe: return empty list
        }
    }
}