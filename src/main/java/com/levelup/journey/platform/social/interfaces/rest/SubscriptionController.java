package com.levelup.journey.platform.social.interfaces.rest;

import com.levelup.journey.platform.social.domain.model.commands.RemoveSubscriptionCommand;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByCommunityIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.domain.services.SubscriptionCommandService;
import com.levelup.journey.platform.social.domain.services.SubscriptionQueryService;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateSubscriptionResource;
import com.levelup.journey.platform.social.interfaces.rest.resources.SubscriptionResource;
import com.levelup.journey.platform.social.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import com.levelup.journey.platform.social.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Subscription Controller
 * REST API for managing community subscriptions
 */
@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Operations related to community subscriptions")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionController(SubscriptionCommandService subscriptionCommandService,
                                 SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STUDENT') or hasAuthority('ROLE_TEACHER') or hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new subscription",
               description = "Subscribe a user to a community. Both students and teachers can subscribe to communities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or already subscribed",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - only students and teachers can subscribe",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<SubscriptionResource> createSubscription(@Valid @RequestBody CreateSubscriptionResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to subscribe to community {} without a valid authenticated user", resource.communityId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = authentication.getName().trim();
        logger.info("Creating subscription: user {} to community {}", userId, resource.communityId());

        try {
            var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource, userId);
            var subscription = subscriptionCommandService.handle(command);

            if (subscription.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            var subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
            return new ResponseEntity<>(subscriptionResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating subscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found", content = @Content)
    })
    public ResponseEntity<SubscriptionResource> getSubscriptionById(@PathVariable String subscriptionId) {
        try {
            var query = new GetSubscriptionByIdQuery(SubscriptionId.of(subscriptionId));
            var subscription = subscriptionQueryService.handle(query);

            if (subscription.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var subscriptionResource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
            return ResponseEntity.ok(subscriptionResource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's subscriptions",
               description = "Retrieve all communities a user is subscribed to")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<List<SubscriptionResource>> getSubscriptionsByUser(@PathVariable String userId) {
        try {
            var query = new GetSubscriptionsByUserIdQuery(UserId.of(userId));
            var subscriptions = subscriptionQueryService.handle(query);

            var subscriptionResources = subscriptions.stream()
                    .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(subscriptionResources);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/community/{communityId}")
    @Operation(summary = "Get community subscribers",
               description = "Retrieve all users subscribed to a community")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully")
    })
    public ResponseEntity<List<SubscriptionResource>> getSubscriptionsByCommunity(@PathVariable String communityId) {
        try {
            var query = new GetSubscriptionsByCommunityIdQuery(CommunityId.of(communityId));
            var subscriptions = subscriptionQueryService.handle(query);

            var subscriptionResources = subscriptions.stream()
                    .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(subscriptionResources);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{subscriptionId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a subscription", description = "Unsubscribe from a community. Any authenticated role can unsubscribe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - only students and teachers can unsubscribe"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<Void> deleteSubscription(@PathVariable String subscriptionId) {
        try {
            var command = new RemoveSubscriptionCommand(SubscriptionId.of(subscriptionId));
            boolean deleted = subscriptionCommandService.handle(command);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
