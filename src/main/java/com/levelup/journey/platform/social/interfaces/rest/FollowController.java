package com.levelup.journey.platform.social.interfaces.rest;

import com.levelup.journey.platform.social.domain.model.commands.RemoveFollowCommand;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowersByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowingByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.domain.services.FollowCommandService;
import com.levelup.journey.platform.social.domain.services.FollowQueryService;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateFollowResource;
import com.levelup.journey.platform.social.interfaces.rest.resources.FollowResource;
import com.levelup.journey.platform.social.interfaces.rest.transform.CreateFollowCommandFromResourceAssembler;
import com.levelup.journey.platform.social.interfaces.rest.transform.FollowResourceFromEntityAssembler;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Follow Controller
 * REST API for managing user follow relationships
 */
@RestController
@RequestMapping(value = "/api/v1/follows", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Follows", description = "Operations related to user follow relationships")
public class FollowController {

    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    private final FollowCommandService followCommandService;
    private final FollowQueryService followQueryService;

    public FollowController(FollowCommandService followCommandService,
                           FollowQueryService followQueryService) {
        this.followCommandService = followCommandService;
        this.followQueryService = followQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new follow relationship",
               description = "Create a follow relationship between two users. The follower is inferred from the authenticated JWT; the payload only requires the user to follow.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Follow relationship created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FollowResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or relationship already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<FollowResource> createFollow(@Valid @RequestBody CreateFollowResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to create follow relationship without a valid authenticated user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String followerId = authentication.getName().trim();
        logger.info("Creating follow relationship: {} -> {}", followerId, resource.followingId());

        try {
            var command = CreateFollowCommandFromResourceAssembler.toCommandFromResource(resource, followerId);
            var follow = followCommandService.handle(command);

            if (follow.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            var followResource = FollowResourceFromEntityAssembler.toResourceFromEntity(follow.get());
            return new ResponseEntity<>(followResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating follow", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{followId}")
    @Operation(summary = "Get follow by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FollowResource.class))),
            @ApiResponse(responseCode = "404", description = "Follow not found", content = @Content)
    })
    public ResponseEntity<FollowResource> getFollowById(@PathVariable String followId) {
        try {
            var query = new GetFollowByIdQuery(FollowId.of(followId));
            var follow = followQueryService.handle(query);

            if (follow.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var followResource = FollowResourceFromEntityAssembler.toResourceFromEntity(follow.get());
            return ResponseEntity.ok(followResource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/followers/{userId}")
    @Operation(summary = "Get user's followers",
               description = "Retrieve all users following a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followers retrieved successfully")
    })
    public ResponseEntity<List<FollowResource>> getFollowers(@PathVariable String userId) {
        try {
            var query = new GetFollowersByUserIdQuery(UserId.of(userId));
            var follows = followQueryService.handle(query);

            var followResources = follows.stream()
                    .map(FollowResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(followResources);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/following/{userId}")
    @Operation(summary = "Get users being followed",
               description = "Retrieve all users that a specific user is following")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Following list retrieved successfully")
    })
    public ResponseEntity<List<FollowResource>> getFollowing(@PathVariable String userId) {
        try {
            var query = new GetFollowingByUserIdQuery(UserId.of(userId));
            var follows = followQueryService.handle(query);

            var followResources = follows.stream()
                    .map(FollowResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(followResources);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{followId}")
    @Operation(summary = "Delete a follow relationship", description = "Unfollow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Follow deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Follow not found")
    })
    public ResponseEntity<Void> deleteFollow(@PathVariable String followId) {
        try {
            var command = new RemoveFollowCommand(FollowId.of(followId));
            boolean deleted = followCommandService.handle(command);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
