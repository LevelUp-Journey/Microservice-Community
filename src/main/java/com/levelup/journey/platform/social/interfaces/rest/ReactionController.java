package com.levelup.journey.platform.social.interfaces.rest;

import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionCommand;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByPostIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.domain.services.ReactionCommandService;
import com.levelup.journey.platform.social.domain.services.ReactionQueryService;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateReactionResource;
import com.levelup.journey.platform.social.interfaces.rest.resources.ReactionResource;
import com.levelup.journey.platform.social.interfaces.rest.transform.CreateReactionCommandFromResourceAssembler;
import com.levelup.journey.platform.social.interfaces.rest.transform.ReactionResourceFromEntityAssembler;
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
 * Reaction Controller
 * REST API for managing reactions to posts
 */
@RestController
@RequestMapping(value = "/api/v1/reactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reactions", description = "Operations related to post reactions")
public class ReactionController {

    private static final Logger logger = LoggerFactory.getLogger(ReactionController.class);

    private final ReactionCommandService reactionCommandService;
    private final ReactionQueryService reactionQueryService;

    public ReactionController(ReactionCommandService reactionCommandService,
                             ReactionQueryService reactionQueryService) {
        this.reactionCommandService = reactionCommandService;
        this.reactionQueryService = reactionQueryService;
    }

    /**
     * Create a new reaction
     */
    @PostMapping
    @Operation(summary = "Create a new reaction",
               description = "Create a new reaction to a post. Users can only have one reaction per post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reaction created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user already reacted to this post",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Reaction with this ID already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ReactionResource> createReaction(@Valid @RequestBody CreateReactionResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to create reaction for post {} without a valid authenticated user", resource.postId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = authentication.getName().trim();
        logger.info("Creating reaction with postId: {}, userId: {}, type: {}",
                resource.postId(), userId, resource.reactionType());

        try {
            var command = CreateReactionCommandFromResourceAssembler.toCommandFromResource(resource, userId);
            var reaction = reactionCommandService.handle(command);

            if (reaction.isEmpty()) {
                logger.warn("Failed to create reaction for post {} by user {} - service returned empty result", resource.postId(), userId);
                return ResponseEntity.badRequest().build();
            }

            var reactionResource = ReactionResourceFromEntityAssembler.toResourceFromEntity(reaction.get());
            logger.info("Reaction created successfully with ID: {}", reactionResource.id());
            return new ResponseEntity<>(reactionResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating reaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating reaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get reaction by ID
     */
    @GetMapping("/{reactionId}")
    @Operation(summary = "Get reaction by ID", description = "Retrieve a specific reaction by its identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReactionResource.class))),
            @ApiResponse(responseCode = "404", description = "Reaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid reaction ID format",
                    content = @Content)
    })
    public ResponseEntity<ReactionResource> getReactionById(@PathVariable String reactionId) {
        logger.info("Getting reaction with ID: {}", reactionId);

        try {
            var query = new GetReactionByIdQuery(ReactionId.of(reactionId));
            var reaction = reactionQueryService.handle(query);

            if (reaction.isEmpty()) {
                logger.info("Reaction not found with ID: {}", reactionId);
                return ResponseEntity.notFound().build();
            }

            var reactionResource = ReactionResourceFromEntityAssembler.toResourceFromEntity(reaction.get());
            return ResponseEntity.ok(reactionResource);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid reaction ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all reactions for a post
     */
    @GetMapping("/post/{postId}")
    @Operation(summary = "Get reactions by post ID",
               description = "Retrieve all reactions for a specific post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid post ID format",
                    content = @Content)
    })
    public ResponseEntity<List<ReactionResource>> getReactionsByPost(@PathVariable String postId) {
        logger.info("Getting reactions for post: {}", postId);

        try {
            var query = new GetReactionsByPostIdQuery(PostId.of(postId));
            var reactions = reactionQueryService.handle(query);

            var reactionResources = reactions.stream()
                    .map(ReactionResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            logger.info("Found {} reactions for post: {}", reactionResources.size(), postId);
            return ResponseEntity.ok(reactionResources);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid post ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all reactions by a user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reactions by user ID",
               description = "Retrieve all reactions made by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format",
                    content = @Content)
    })
    public ResponseEntity<List<ReactionResource>> getReactionsByUser(@PathVariable String userId) {
        logger.info("Getting reactions for user: {}", userId);

        try {
            var query = new GetReactionsByUserIdQuery(UserId.of(userId));
            var reactions = reactionQueryService.handle(query);

            var reactionResources = reactions.stream()
                    .map(ReactionResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            logger.info("Found {} reactions for user: {}", reactionResources.size(), userId);
            return ResponseEntity.ok(reactionResources);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a reaction
     */
    @DeleteMapping("/{reactionId}")
    @Operation(summary = "Delete a reaction",
               description = "Remove a reaction from a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reaction deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid reaction ID format",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteReaction(@PathVariable String reactionId) {
        logger.info("Deleting reaction with ID: {}", reactionId);

        try {
            var command = new RemoveReactionCommand(ReactionId.of(reactionId));
            boolean deleted = reactionCommandService.handle(command);

            if (!deleted) {
                logger.info("Reaction not found with ID: {}", reactionId);
                return ResponseEntity.notFound().build();
            }

            logger.info("Reaction deleted successfully with ID: {}", reactionId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            logger.error("Invalid reaction ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
