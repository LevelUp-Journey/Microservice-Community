package com.levelup.journey.platform.post.interfaces.rest;

import com.levelup.journey.platform.post.domain.model.commands.DeletePostCommand;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.application.internal.queryservices.PostQueryServiceImpl;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreatePostResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.PagedResponse;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;
import com.levelup.journey.platform.post.interfaces.rest.transform.CreatePostCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.PostResourceFromEntityAssembler;
import com.levelup.journey.platform.shared.domain.acl.SocialRelationshipService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Post Controller
 * REST API for managing posts and comments
 */
@RestController
@RequestMapping(value = "/api/v1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Posts", description = "Operations related to posts and comments")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;
    private final PostQueryServiceImpl postQueryServiceImpl;
    private final SocialRelationshipService socialRelationshipService;
    private final PostResourceFromEntityAssembler postResourceAssembler;

    public PostController(PostCommandService postCommandService,
                         PostQueryService postQueryService,
                         PostQueryServiceImpl postQueryServiceImpl,
                         SocialRelationshipService socialRelationshipService,
                         PostResourceFromEntityAssembler postResourceAssembler) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
        this.postQueryServiceImpl = postQueryServiceImpl;
        this.socialRelationshipService = socialRelationshipService;
        this.postResourceAssembler = postResourceAssembler;
    }

    /**
     * Helper method to get the current authenticated user ID
     * @return the user ID if authenticated, null otherwise
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            return authentication.getName().trim();
        }
        return null;
    }

    /**
     * Create a new post
     */
    @PostMapping
    @Operation(
        summary = "Create a new post",
        description = "Publish a new post in a community. Only community owners and subscribed teachers can post. "
                + "The authorId is resolved from the authenticated JWT and the related profile is fetched internally, so clients do not need to send any author identifiers."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient permissions",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - only community owners and subscribed teachers can post",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Post with this ID already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<PostResource> createPost(@Valid @RequestBody CreatePostResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to create post without a valid authenticated user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authorId = authentication.getName().trim();
        logger.info("Creating post in communityId: {}, authorId: {}",
                   resource.communityId(), authorId);

        try {
            var command = CreatePostCommandFromResourceAssembler.toCommandFromResource(resource, authorId);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to create post in community: {} - service returned empty result", resource.communityId());
                return ResponseEntity.badRequest().build();
            }

            var postResource = postResourceAssembler.toResourceFromEntity(post.get(), authorId);
            logger.info("Post created successfully with ID: {}", postResource.id());
            return new ResponseEntity<>(postResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating post in community: {} - {}", resource.communityId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating post in community: {}", resource.communityId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{postId}")
    @Operation(summary = "Get post by ID", description = "Retrieve a specific post by its identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid post ID format",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content)
    })
    public ResponseEntity<PostResource> getPostById(@PathVariable String postId) {
        logger.info("Retrieving post with ID: {}", postId);

        try {
            var query = new GetPostByIdQuery(PostId.of(postId));
            var post = postQueryService.handle(query);

            if (post.isEmpty()) {
                logger.warn("Post not found with ID: {}", postId);
                return ResponseEntity.notFound().build();
            }

            // Get current user ID if authenticated
            String currentUserId = getCurrentUserId();
            var postResource = postResourceAssembler.toResourceFromEntity(post.get(), currentUserId);
            logger.debug("Post retrieved successfully with ID: {}", postId);
            return ResponseEntity.ok(postResource);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid post ID format: {} - {}", postId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving post with ID: {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all posts
     */
    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieve all posts from all communities with pagination, ordered by creation date (most recent first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully with pagination metadata",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class)))
    })
    public ResponseEntity<PagedResponse<PostResource>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Retrieving all posts - page: {}, size: {}", page, size);

        try {
            // Validate parameters
            if (page < 0) {
                logger.warn("Invalid page value: {}. Must be non-negative", page);
                return ResponseEntity.badRequest().build();
            }
            if (size <= 0 || size > 100) {
                logger.warn("Invalid size value: {}. Must be between 1 and 100", size);
                return ResponseEntity.badRequest().build();
            }

            var query = new GetAllPostsQuery(page, size);
            var posts = postQueryService.handle(query);
            var totalElements = postQueryServiceImpl.countAllPosts();

            // Get current user ID if authenticated
            String currentUserId = getCurrentUserId();
            var postResources = posts.stream()
                    .map(post -> postResourceAssembler.toResourceFromEntity(post, currentUserId))
                    .collect(Collectors.toList());

            var pagedResponse = PagedResponse.of(postResources, page, size, totalElements);
            logger.info("Retrieved {} posts successfully for page {} of {}", postResources.size(), page, pagedResponse.totalPages());
            return ResponseEntity.ok(pagedResponse);

        } catch (Exception e) {
            logger.error("Unexpected error retrieving all posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get posts by community ID
     */
    @GetMapping("/community/{communityId}")
    @Operation(summary = "Get posts by community", description = "Retrieve all posts from a specific community with pagination, ordered by creation date (most recent first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully with pagination metadata",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid community ID format",
                    content = @Content)
    })
    public ResponseEntity<PagedResponse<PostResource>> getPostsByCommunity(
            @PathVariable String communityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Retrieving posts for community with ID: {}, page: {}, size: {}", communityId, page, size);

        try {
            // Validate parameters
            if (page < 0) {
                logger.warn("Invalid page value: {}. Must be non-negative", page);
                return ResponseEntity.badRequest().build();
            }
            if (size <= 0 || size > 100) {
                logger.warn("Invalid size value: {}. Must be between 1 and 100", size);
                return ResponseEntity.badRequest().build();
            }

            var communityIdVO = CommunityId.of(communityId);
            var query = new GetPostsByCommunityIdQuery(communityIdVO, page, size);
            var posts = postQueryService.handle(query);
            var totalElements = postQueryServiceImpl.countPostsByCommunity(communityIdVO);

            // Get current user ID if authenticated
            String currentUserId = getCurrentUserId();
            var postResources = posts.stream()
                    .map(post -> postResourceAssembler.toResourceFromEntity(post, currentUserId))
                    .collect(Collectors.toList());

            var pagedResponse = PagedResponse.of(postResources, page, size, totalElements);
            logger.info("Retrieved {} posts for community {} on page {} of {}",
                       postResources.size(), communityId, page, pagedResponse.totalPages());
            return ResponseEntity.ok(pagedResponse);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid community ID format: {} - {}", communityId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving posts for community: {}", communityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Delete a post
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Delete a post. Only the post author, community owner, or admin can delete posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid post ID format",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - only post author, community owner, or admin can delete posts",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to delete post {} without a valid authenticated user", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String requesterId = authentication.getName().trim();
        logger.info("Deleting post with ID: {}, requesterId: {}", postId, requesterId);

        try {
            var command = DeletePostCommand.of(postId, requesterId);
            boolean deleted = postCommandService.handle(command);

            if (!deleted) {
                logger.warn("Failed to delete post with ID: {} - post not found", postId);
                return ResponseEntity.notFound().build();
            }

            logger.info("Post deleted successfully with ID: {}", postId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            logger.error("Validation error deleting post with ID: {} - {}", postId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error deleting post with ID: {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
