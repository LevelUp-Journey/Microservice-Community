package com.levelup.journey.platform.post.interfaces.rest;

import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.interfaces.rest.resources.AddCommentResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreatePostResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;
import com.levelup.journey.platform.post.interfaces.rest.transform.AddCommentCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.CreatePostCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.PostResourceFromEntityAssembler;
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

    public PostController(PostCommandService postCommandService,
                         PostQueryService postQueryService) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
    }

    /**
     * Create a new post
     */
    @PostMapping
    @Operation(summary = "Create a new post", description = "Publish a new post in a community")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or post already exists",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Post with this ID already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<PostResource> createPost(@Valid @RequestBody CreatePostResource resource) {
        logger.info("Creating post with ID: {}, title: {}, communityId: {}, authorId: {}",
                   resource.id(), resource.title(), resource.communityId(), resource.authorId());

        try {
            var command = CreatePostCommandFromResourceAssembler.toCommandFromResource(resource);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to create post with ID: {} - service returned empty result", resource.id());
                return ResponseEntity.badRequest().build();
            }

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
            logger.info("Post created successfully with ID: {}", postResource.id());
            return new ResponseEntity<>(postResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating post with ID: {} - {}", resource.id(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating post with ID: {}", resource.id(), e);
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
    @Operation(summary = "Get all posts", description = "Retrieve all posts from all communities")
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
    @Operation(summary = "Get posts by community", description = "Retrieve all posts from a specific community")
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
     * Add a comment to a post
     */
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
        logger.info("Adding comment with ID: {} to post: {}, authorId: {}",
                   resource.commentId(), postId, resource.authorId());

        try {
            var command = AddCommentCommandFromResourceAssembler.toCommandFromResource(postId, resource);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                logger.warn("Failed to add comment with ID: {} to post: {} - post not found or comment already exists",
                           resource.commentId(), postId);
                return ResponseEntity.notFound().build();
            }

            var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
            logger.info("Comment added successfully with ID: {} to post: {}", resource.commentId(), postId);
            return ResponseEntity.ok(postResource);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error adding comment with ID: {} to post: {} - {}",
                        resource.commentId(), postId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error adding comment with ID: {} to post: {}", resource.commentId(), postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
