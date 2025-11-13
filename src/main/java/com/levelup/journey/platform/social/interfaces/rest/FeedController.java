package com.levelup.journey.platform.social.interfaces.rest;

import com.levelup.journey.platform.social.domain.model.queries.GetFeedByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowingByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.domain.services.FeedQueryService;
import com.levelup.journey.platform.social.domain.services.FollowQueryService;
import com.levelup.journey.platform.social.domain.services.SubscriptionQueryService;
import com.levelup.journey.platform.social.interfaces.rest.resources.FeedSourcesResource;
import com.levelup.journey.platform.social.interfaces.rest.resources.PaginatedFeedResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Feed Controller
 * REST API for managing user feeds and content discovery
 */
@RestController
@RequestMapping(value = "/api/v1/feed", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Feed", description = "Operations related to personalized user feeds and content discovery")
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private final FeedQueryService feedQueryService;
    private final FollowQueryService followQueryService;
    private final SubscriptionQueryService subscriptionQueryService;

    public FeedController(FeedQueryService feedQueryService,
                         FollowQueryService followQueryService,
                         SubscriptionQueryService subscriptionQueryService) {
        this.feedQueryService = feedQueryService;
        this.followQueryService = followQueryService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    /**
     * Get feed sources for a user
     * Returns the list of user IDs and community IDs that contribute to the user's feed
     */
    @GetMapping("/sources/{userId}")
    @Operation(
            summary = "Get feed sources for a user",
            description = "Retrieve all sources (followed users and subscribed communities) that contribute to a user's personalized feed"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feed sources retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FeedSourcesResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID format",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    public ResponseEntity<FeedSourcesResource> getFeedSources(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable String userId
    ) {
        logger.info("Getting feed sources for user: {}", userId);

        try {
            UserId userIdVO = UserId.of(userId);

            // Get followed users
            var followingQuery = new GetFollowingByUserIdQuery(userIdVO);
            var following = followQueryService.handle(followingQuery);
            List<String> followedUserIds = following.stream()
                    .map(follow -> follow.followingId().value())
                    .collect(Collectors.toList());

            // Get subscribed communities
            var subscriptionsQuery = new GetSubscriptionsByUserIdQuery(userIdVO);
            var subscriptions = subscriptionQueryService.handle(subscriptionsQuery);
            List<String> subscribedCommunityIds = subscriptions.stream()
                    .map(subscription -> subscription.communityId().value())
                    .collect(Collectors.toList());

            var feedSourcesResource = new FeedSourcesResource(
                    userId,
                    followedUserIds,
                    subscribedCommunityIds,
                    followedUserIds.size() + subscribedCommunityIds.size()
            );

            logger.info("Retrieved feed sources for user {}: {} followed users, {} subscribed communities",
                    userId, followedUserIds.size(), subscribedCommunityIds.size());

            return ResponseEntity.ok(feedSourcesResource);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting feed sources for user: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get enriched feed for a user with pagination
     * Returns a paginated list of posts with complete metadata including community information
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "Get feed for a user",
            description = "Retrieve a personalized feed for a user based on their follows and subscriptions. " +
                    "Returns posts with complete metadata including community name, author information, and reactions."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feed retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaginatedFeedResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content
            )
    })
    public ResponseEntity<PaginatedFeedResource> getFeed(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable String userId,

            @Parameter(description = "Number of items to return (default: 20, max: 100)")
            @RequestParam(defaultValue = "20") int limit,

            @Parameter(description = "Number of items to skip (default: 0)")
            @RequestParam(defaultValue = "0") int offset
    ) {
        logger.info("Getting enriched feed for user: {}, limit: {}, offset: {}", userId, limit, offset);

        try {
            // Validate pagination parameters
            if (limit <= 0 || limit > 100) {
                logger.warn("Invalid limit value: {}. Must be between 1 and 100", limit);
                return ResponseEntity.badRequest().build();
            }

            if (offset < 0) {
                logger.warn("Invalid offset value: {}. Must be non-negative", offset);
                return ResponseEntity.badRequest().build();
            }

            UserId userIdVO = UserId.of(userId);
            var query = new GetFeedByUserIdQuery(userIdVO, limit, offset);
            var feed = feedQueryService.handle(query);

            logger.info("Retrieved enriched feed with {} items for user {}", feed.content().size(), userId);

            return ResponseEntity.ok(feed);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting feed for user: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get feed statistics for a user
     */
    @GetMapping("/stats/{userId}")
    @Operation(
            summary = "Get feed statistics",
            description = "Get statistics about a user's feed including number of follows, subscriptions, and potential content sources"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format")
    })
    public ResponseEntity<FeedStatsResource> getFeedStats(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable String userId
    ) {
        logger.info("Getting feed statistics for user: {}", userId);

        try {
            UserId userIdVO = UserId.of(userId);

            // Get following count
            var followingQuery = new GetFollowingByUserIdQuery(userIdVO);
            var following = followQueryService.handle(followingQuery);
            int followingCount = following.size();

            // Get subscriptions count
            var subscriptionsQuery = new GetSubscriptionsByUserIdQuery(userIdVO);
            var subscriptions = subscriptionQueryService.handle(subscriptionsQuery);
            int subscriptionsCount = subscriptions.size();

            var stats = new FeedStatsResource(
                    userId,
                    followingCount,
                    subscriptionsCount,
                    followingCount + subscriptionsCount
            );

            logger.info("Feed stats for user {}: {} following, {} subscriptions",
                    userId, followingCount, subscriptionsCount);

            return ResponseEntity.ok(stats);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Feed Statistics Resource
     */
    public record FeedStatsResource(
            String userId,
            int followingCount,
            int subscriptionsCount,
            int totalSources
    ) {
    }
}
