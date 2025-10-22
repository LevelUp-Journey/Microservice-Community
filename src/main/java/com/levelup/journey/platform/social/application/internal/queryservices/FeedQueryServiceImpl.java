package com.levelup.journey.platform.social.application.internal.queryservices;

import com.levelup.journey.platform.social.domain.model.queries.GetFeedByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.repositories.FollowRepository;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.services.FeedQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed Query Service Implementation
 * Generates personalized feeds for users based on their follows and subscriptions
 */
@Service
public class FeedQueryServiceImpl implements FeedQueryService {

    private static final Logger logger = LoggerFactory.getLogger(FeedQueryServiceImpl.class);

    private final FollowRepository followRepository;
    private final SubscriptionRepository subscriptionRepository;

    public FeedQueryServiceImpl(FollowRepository followRepository,
                               SubscriptionRepository subscriptionRepository) {
        this.followRepository = followRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<String> handle(GetFeedByUserIdQuery query) {
        logger.info("Generating feed for user: {}, limit: {}, offset: {}",
                query.userId(), query.limit(), query.offset());

        try {
            List<String> feedSources = new ArrayList<>();

            // Get users the current user is following
            var following = followRepository.findByFollowerId(query.userId());

            logger.info("User {} is following {} users", query.userId(), following.size());

            // Add followed user IDs to feed sources
            following.forEach(follow -> feedSources.add(follow.followingId().value()));

            // Get communities the user is subscribed to
            var subscriptions = subscriptionRepository.findByUserId(query.userId());

            logger.info("User {} is subscribed to {} communities", query.userId(), subscriptions.size());

            // Add subscribed community IDs to feed sources
            subscriptions.forEach(subscription -> feedSources.add(subscription.communityId().value()));

            logger.info("Generated feed with {} sources for user {}", feedSources.size(), query.userId());

            // Return the list of user IDs and community IDs that should be used to fetch posts
            // The actual post fetching would be done via ACL to the Post BC or in a separate service
            return feedSources;

        } catch (Exception e) {
            logger.error("Error generating feed for user: {}", query.userId(), e);
            return new ArrayList<>();
        }
    }
}
