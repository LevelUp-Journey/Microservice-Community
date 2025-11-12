package com.levelup.journey.platform.social.interfaces.rest;

import com.levelup.journey.platform.social.domain.model.commands.AddReactionCommand;
import com.levelup.journey.platform.social.domain.model.commands.RemoveReactionByUserAndPostCommand;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionByUserAndPostQuery;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.domain.services.ReactionCommandService;
import com.levelup.journey.platform.social.domain.services.ReactionQueryService;
import com.levelup.journey.platform.social.interfaces.rest.resources.ReactionResource;
import com.levelup.journey.platform.social.interfaces.rest.transform.ReactionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Add a like to a post
     */
    @PostMapping("/user/{userId}/post/{postId}")
    @Operation(summary = "Add a like to a post",
               description = "Add a like reaction to a post for a specific user. Fails if user already liked the post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Like added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID or post ID format",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User already has a reaction on this post",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ReactionResource> addLike(
            @PathVariable String userId,
            @PathVariable String postId) {
        logger.info("Adding like for user: {} on post: {}", userId, postId);

        try {
            var command = new AddReactionCommand(PostId.of(postId), UserId.of(userId), ReactionType.LIKE);
            var reaction = reactionCommandService.handle(command);

            if (reaction.isEmpty()) {
                logger.error("Failed to add like for user: {} on post: {}", userId, postId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            var reactionResource = ReactionResourceFromEntityAssembler.toResourceFromEntity(reaction.get());
            logger.info("Like added successfully with ID: {} for user: {} on post: {}",
                       reactionResource.id(), userId, postId);
            return ResponseEntity.status(HttpStatus.CREATED).body(reactionResource);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID or post ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            logger.error("User already has a reaction on this post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Unexpected error adding like for user: {} on post: {}", userId, postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get reaction by user and post
     */
    @GetMapping("/user/{userId}/post/{postId}")
    @Operation(summary = "Get user's reaction on a post",
               description = "Retrieve the reaction of a specific user on a specific post, if it exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReactionResource.class))),
            @ApiResponse(responseCode = "204", description = "No reaction found - user has not reacted to this post",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid user ID or post ID format",
                    content = @Content)
    })
    public ResponseEntity<ReactionResource> getReactionByUserAndPost(
            @PathVariable String userId,
            @PathVariable String postId) {
        logger.info("Getting reaction for user: {} on post: {}", userId, postId);

        try {
            var query = new GetReactionByUserAndPostQuery(UserId.of(userId), PostId.of(postId));
            var reaction = reactionQueryService.handle(query);

            if (reaction.isEmpty()) {
                logger.info("No reaction found for user: {} on post: {}", userId, postId);
                return ResponseEntity.noContent().build();
            }

            var reactionResource = ReactionResourceFromEntityAssembler.toResourceFromEntity(reaction.get());
            logger.info("Found reaction with ID: {} for user: {} on post: {}",
                       reactionResource.id(), userId, postId);
            return ResponseEntity.ok(reactionResource);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID or post ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove a like from a post
     */
    @DeleteMapping("/user/{userId}/post/{postId}")
    @Operation(summary = "Remove a like from a post",
               description = "Remove the like reaction from a post for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Like removed successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No reaction found for this user on this post",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid user ID or post ID format",
                    content = @Content)
    })
    public ResponseEntity<Void> removeLike(
            @PathVariable String userId,
            @PathVariable String postId) {
        logger.info("Removing like for user: {} on post: {}", userId, postId);

        try {
            var command = new RemoveReactionByUserAndPostCommand(PostId.of(postId), UserId.of(userId));
            boolean removed = reactionCommandService.handle(command);

            if (!removed) {
                logger.info("No reaction found to remove for user: {} on post: {}", userId, postId);
                return ResponseEntity.notFound().build();
            }

            logger.info("Like removed successfully for user: {} on post: {}", userId, postId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID or post ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error removing like for user: {} on post: {}", userId, postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
