package com.levelup.journey.platform.post.interfaces.rest;

import com.levelup.journey.platform.post.domain.model.commands.DeleteCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.DeletePostCommand;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.interfaces.rest.resources.AddCommentResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.EditCommentResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreatePostResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;
import com.levelup.journey.platform.post.interfaces.rest.transform.AddCommentCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.EditCommentCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.CreatePostCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.PostResourceFromEntityAssembler;
import com.levelup.journey.platform.shared.domain.acl.SocialRelationshipService;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
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
    private final SocialRelationshipService socialRelationshipService;

    public PostController(PostCommandService postCommandService,
                         PostQueryService postQueryService,
                         SocialRelationshipService socialRelationshipService) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
        this.socialRelationshipService = socialRelationshipService;
    }

    /**
     * Create a new post
     */
    @PostMapping
    @Operation(summary = "Create a new post", description = "Publish a new post in a community. Only community owners and subscribed teachers can post.")
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
        logger.info("Creating post with title: {}, communityId: {}, authorId: {}",
                   resource.title(), resource.communityId(), authorId);

        try {
            var command = CreatePostCommandFromResourceAssembler.toCommandFromResource(resource, authorId);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to create post with title: {} - service returned empty result", resource.title());
                return ResponseEntity.badRequest().build();
            }

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
            logger.info("Post created successfully with ID: {}", postResource.id());
            return new ResponseEntity<>(postResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating post with title: {} - {}", resource.title(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating post with title: {}", resource.title(), e);
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

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
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
    @Operation(summary = "Get all posts", description = "Retrieve all posts from all communities, ordered by creation date (most recent first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)))
    })
    public ResponseEntity<List<PostResource>> getAllPosts() {
        logger.info("Retrieving all posts");

        try {
            var query = new GetAllPostsQuery();
            var posts = postQueryService.handle(query);

            var postResources = posts.stream()
                    .map(PostResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} posts successfully", postResources.size());
            return ResponseEntity.ok(postResources);

        } catch (Exception e) {
            logger.error("Unexpected error retrieving all posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get posts by community ID
     */
    @GetMapping("/community/{communityId}")
    @Operation(summary = "Get posts by community", description = "Retrieve all posts from a specific community, ordered by creation date (most recent first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid community ID format",
                    content = @Content)
    })
    public ResponseEntity<List<PostResource>> getPostsByCommunity(@PathVariable String communityId) {
        logger.info("Retrieving posts for community with ID: {}", communityId);

        try {
            var query = new GetPostsByCommunityIdQuery(CommunityId.of(communityId));
            var posts = postQueryService.handle(query);

            var postResources = posts.stream()
                    .map(PostResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} posts for community {}", postResources.size(), communityId);
            return ResponseEntity.ok(postResources);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid community ID format: {} - {}", communityId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving posts for community: {}", communityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get feed posts for a user (from followed users and subscribed communities)
     */
    @GetMapping("/feed/{userId}")
    @Operation(summary = "Get user feed posts", description = "Retrieve posts from users and communities that the user follows, ordered by creation date (most recent first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feed posts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<List<PostResource>> getUserFeed(@PathVariable String userId,
                                                          @RequestParam(defaultValue = "20") int limit,
                                                          @RequestParam(defaultValue = "0") int offset) {
        logger.info("Retrieving feed posts for user: {}, limit: {}, offset: {}", userId, limit, offset);

        try {
            // Validate parameters
            if (limit <= 0 || limit > 100) {
                logger.warn("Invalid limit value: {}. Must be between 1 and 100", limit);
                return ResponseEntity.badRequest().build();
            }
            if (offset < 0) {
                logger.warn("Invalid offset value: {}. Must be non-negative", offset);
                return ResponseEntity.badRequest().build();
            }

            // Get user's followed users
            var followedUserIds = socialRelationshipService.getFollowing(userId);

            // Get user's subscribed communities
            var subscribedCommunityIds = socialRelationshipService.getSubscribedCommunities(userId);

            // Get all posts and filter by feed sources
            var query = new GetAllPostsQuery();
            var allPosts = postQueryService.handle(query);

            // Filter posts: include posts from followed users OR from subscribed communities
            var feedPosts = allPosts.stream()
                    .filter(post -> followedUserIds.contains(post.authorId().value()) ||
                                   subscribedCommunityIds.contains(post.communityId().value()))
                    .sorted((p1, p2) -> p2.createdAt().compareTo(p1.createdAt())) // Most recent first
                    .skip(offset)
                    .limit(limit)
                    .map(PostResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} feed posts for user {}", feedPosts.size(), userId);
            return ResponseEntity.ok(feedPosts);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID format: {} - {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving feed posts for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/{postId}/comments")
    @Operation(summary = "Add comment to post", description = "Add a new comment to an existing post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or comment already exists",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Comment with this ID already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<PostResource> addComment(@PathVariable String postId,
                                                   @Valid @RequestBody AddCommentResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to add comment to post {} without a valid authenticated user", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authorId = authentication.getName().trim();
        logger.info("Adding comment to post: {}, authorId: {}",
                   postId, authorId);

        try {
            var command = AddCommentCommandFromResourceAssembler.toCommandFromResource(postId, resource, authorId);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to add comment to post: {} - post not found or comment already exists",
                           postId);
                return ResponseEntity.notFound().build();
            }

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
            logger.info("Comment added successfully to post: {}", postId);
            return ResponseEntity.ok(postResource);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error adding comment to post: {} - {}",
                        postId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error adding comment to post: {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Edit a comment
     */
    @PutMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "Edit comment", description = "Edit a comment's content. Only the comment author can edit their comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment edited successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or comment not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - only comment author can edit comments",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post or comment not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<PostResource> editComment(@PathVariable String postId,
                                                    @PathVariable String commentId,
                                                    @RequestParam String requesterId,
                                                    @Valid @RequestBody EditCommentResource resource) {
        logger.info("Editing comment: {} on post: {}, requesterId: {}",
                   commentId, postId, requesterId);

        try {
            var command = EditCommentCommandFromResourceAssembler.toCommandFromResource(postId, commentId, requesterId, resource);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to edit comment: {} on post: {} - comment not found or access denied",
                           commentId, postId);
                return ResponseEntity.notFound().build();
            }

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
            logger.info("Comment edited successfully: {} on post: {}", commentId, postId);
            return ResponseEntity.ok(postResource);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error editing comment: {} on post: {} - {}",
                        commentId, postId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error editing comment: {} on post: {}", commentId, postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a comment
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment. Only the comment author, post author, or admin can delete comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid comment ID format",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions to delete comment",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post or comment not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<PostResource> deleteComment(@PathVariable String postId,
                                                      @PathVariable String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Attempted to delete comment {} from post {} without a valid authenticated user", commentId, postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String requesterId = authentication.getName().trim();
        logger.info("Deleting comment: {} from post: {}, requesterId: {}", commentId, postId, requesterId);

        try {
            var command = new DeleteCommentCommand(PostId.of(postId), CommentId.of(commentId), UserId.of(requesterId));
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to delete comment: {} from post: {} - comment not found or access denied",
                           commentId, postId);
                return ResponseEntity.notFound().build();
            }

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
            logger.info("Comment deleted successfully: {} from post: {}", commentId, postId);
            return ResponseEntity.ok(postResource);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error deleting comment: {} from post: {} - {}",
                        commentId, postId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error deleting comment: {} from post: {}", commentId, postId, e);
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
