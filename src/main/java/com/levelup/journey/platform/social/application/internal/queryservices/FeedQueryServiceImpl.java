package com.levelup.journey.platform.social.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.queries.GetPostsBySourceIdsQuery;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.application.internal.outboundservices.acl.ExternalUserService;
import com.levelup.journey.platform.post.interfaces.rest.resources.ReactionSummaryResource;
import com.levelup.journey.platform.social.domain.model.queries.GetFeedByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByPostIdQuery;
import com.levelup.journey.platform.social.domain.model.repositories.FollowRepository;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.services.FeedQueryService;
import com.levelup.journey.platform.social.domain.services.ReactionQueryService;
import com.levelup.journey.platform.social.interfaces.rest.resources.FeedItemResource;
import com.levelup.journey.platform.social.interfaces.rest.resources.PaginatedFeedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Feed Query Service Implementation
 * Generates personalized feeds for users based on their follows and subscriptions
 */
@Service
public class FeedQueryServiceImpl implements FeedQueryService {

    private static final Logger logger = LoggerFactory.getLogger(FeedQueryServiceImpl.class);

    private final FollowRepository followRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostQueryService postQueryService;
    private final CommunityQueryService communityQueryService;
    private final ReactionQueryService reactionQueryService;
    private final ExternalUserService externalUserService;

    public FeedQueryServiceImpl(FollowRepository followRepository,
                               SubscriptionRepository subscriptionRepository,
                               PostQueryService postQueryService,
                               CommunityQueryService communityQueryService,
                               ReactionQueryService reactionQueryService,
                               ExternalUserService externalUserService) {
        this.followRepository = followRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.postQueryService = postQueryService;
        this.communityQueryService = communityQueryService;
        this.reactionQueryService = reactionQueryService;
        this.externalUserService = externalUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedFeedResource handle(GetFeedByUserIdQuery query) {
        logger.info("Generating enriched feed for user: {}, limit: {}, offset: {}",
                query.userId(), query.limit(), query.offset());

        try {
            List<String> feedSources = new ArrayList<>();

            // Get users the current user is following
            var following = followRepository.findByFollowerId(query.userId());
            logger.info("User {} is following {} users", query.userId(), following.size());
            following.forEach(follow -> feedSources.add(follow.followingId().value()));

            // Get communities the user is subscribed to
            var subscriptions = subscriptionRepository.findByUserId(query.userId());
            logger.info("User {} is subscribed to {} communities", query.userId(), subscriptions.size());
            subscriptions.forEach(subscription -> feedSources.add(subscription.communityId().value()));

            // If no sources, return empty feed
            if (feedSources.isEmpty()) {
                logger.info("User {} has no feed sources", query.userId());
                return new PaginatedFeedResource(List.of(), query.offset() / query.limit(), query.limit(), 0);
            }

            // Fetch posts from all sources
            var postsQuery = new GetPostsBySourceIdsQuery(feedSources, query.limit(), query.offset());
            var posts = postQueryService.handle(postsQuery);

            logger.info("Retrieved {} posts for user {}'s feed", posts.size(), query.userId());

            // Enrich posts with community metadata
            List<FeedItemResource> enrichedFeedItems = posts.stream()
                    .map(post -> {
                        // Fetch community data
                        var communityQuery = new GetCommunityByIdQuery(CommunityId.of(post.communityId().value()));
                        var community = communityQueryService.handle(communityQuery);

                        String communityName = community.map(c -> c.name()).orElse(null);
                        String communityImageUrl = community.map(c -> c.imageUrl().isEmpty() ? null : c.imageUrl().url()).orElse(null);

                        // Fetch reactions for this post
                        var reactionsQuery = new GetReactionsByPostIdQuery(PostId.of(post.id().value()));
                        var reactions = reactionQueryService.handle(reactionsQuery);

                        // Calculate aggregated counts by reaction type
                        Map<String, Integer> reactionCounts = reactions.stream()
                                .collect(Collectors.groupingBy(
                                        reaction -> reaction.reactionType().name(),
                                        Collectors.summingInt(reaction -> 1)
                                ));

                        // Determine current user's reaction
                        String userReaction = reactions.stream()
                                .filter(reaction -> reaction.userId().value().equals(query.userId().value()))
                                .findFirst()
                                .map(reaction -> reaction.reactionType().name())
                                .orElse(null);

                        ReactionSummaryResource reactionSummary = new ReactionSummaryResource(
                                reactionCounts.isEmpty() ? new HashMap<>() : reactionCounts,
                                userReaction
                        );

                        // Fetch author data from user context
                        String authorName = externalUserService.fetchUsernameByUserId(post.authorId().value());
                        String authorProfileUrl = externalUserService.fetchProfileUrlByUserId(post.authorId().value());

                        return new FeedItemResource(
                                post.id().value(),
                                post.communityId().value(),
                                communityName,
                                communityImageUrl,
                                post.authorId().value(),
                                post.authorProfileId().value(),
                                authorName,
                                authorProfileUrl,
                                post.content(),
                                post.imageUrl().isEmpty() ? null : post.imageUrl().url(),
                                post.createdAt(),
                                reactionSummary
                        );
                    })
                    .collect(Collectors.toList());

            int page = query.offset() / query.limit();
            // Note: totalElements would ideally come from a count query, but for now we estimate
            long totalElements = enrichedFeedItems.size();

            logger.info("Generated enriched feed with {} items for user {}", enrichedFeedItems.size(), query.userId());

            return new PaginatedFeedResource(enrichedFeedItems, page, query.limit(), totalElements);

        } catch (Exception e) {
            logger.error("Error generating enriched feed for user: {}", query.userId(), e);
            return new PaginatedFeedResource(List.of(), 0, query.limit(), 0);
        }
    }
}
