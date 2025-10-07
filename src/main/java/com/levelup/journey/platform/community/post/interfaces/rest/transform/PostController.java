package com.levelup.journey.platform.community.post.interfaces.rest.transform;

import com.levelup.journey.platform.community.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.community.post.domain.model.commands.CreatePostCommand;
import com.levelup.journey.platform.community.post.domain.model.commands.DeletePostCommand;
import com.levelup.journey.platform.community.post.domain.model.commands.UpdatePostCommand;
import com.levelup.journey.platform.community.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.community.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.community.post.domain.model.queries.GetPostsByUserIdQuery;
import com.levelup.journey.platform.community.post.domain.services.PostCommandService;
import com.levelup.journey.platform.community.post.domain.services.PostQueryService;
import com.levelup.journey.platform.community.post.interfaces.rest.resources.CreatePostResource;
import com.levelup.journey.platform.community.post.interfaces.rest.resources.PostResource;
import com.levelup.journey.platform.community.post.interfaces.rest.resources.UpdatePostResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Post operations.
 * Provides endpoints for creating, reading, updating, and deleting posts.
 */
@Tag(name = "Posts", description = "Endpoints for managing posts in the community platform")
@RestController
@RequestMapping(value = "/api/v1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    public PostController(PostCommandService postCommandService, PostQueryService postQueryService) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
    }

    /**
     * Transform Post entity to PostResource.
     */
    private PostResource toResource(Post post) {
        return new PostResource(
                post.getId(),
                post.getUserId().userId(),
                post.getTitle().value(),
                post.getBody().value(),
                post.getImageUrl() != null ? post.getImageUrl().value() : null,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    /**
     * Create a new post.
     * Only users with DOCENTE role should be able to access this endpoint.
     */
    @Operation(
            summary = "Create a new post",
            description = "Creates a new post in the community. Only users with DOCENTE role can create posts."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Post created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to create posts (not a Docente)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<PostResource> createPost(
            @Valid @RequestBody CreatePostResource resource
    ) {
        try {
            var command = new CreatePostCommand(
                    resource.userId(),
                    resource.title(),
                    resource.body(),
                    resource.imageUrl()
            );

            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(toResource(post.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get a post by ID.
     */
    @Operation(
            summary = "Get post by ID",
            description = "Retrieves a specific post by its ID. Returns only non-deleted posts."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Post found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found or has been deleted",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostResource> getPostById(
            @Parameter(description = "ID of the post to retrieve", example = "1")
            @PathVariable Long postId
    ) {
        var query = new GetPostByIdQuery(postId);
        var post = postQueryService.handle(query);

        return post.map(value -> ResponseEntity.ok(toResource(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all posts.
     */
    @Operation(
            summary = "Get all posts",
            description = "Retrieves all non-deleted posts ordered by creation date (newest first)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Posts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<PostResource>> getAllPosts() {
        var query = new GetAllPostsQuery();
        var posts = postQueryService.handle(query);

        var resources = posts.stream()
                .map(this::toResource)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Get posts by user ID.
     */
    @Operation(
            summary = "Get posts by user",
            description = "Retrieves all non-deleted posts created by a specific user, ordered by creation date (newest first)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Posts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)
                    )
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResource>> getPostsByUserId(
            @Parameter(description = "ID of the user whose posts to retrieve", example = "1")
            @PathVariable Long userId
    ) {
        var query = new GetPostsByUserIdQuery(userId);
        var posts = postQueryService.handle(query);

        var resources = posts.stream()
                .map(this::toResource)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Update a post.
     */
    @Operation(
            summary = "Update a post",
            description = "Updates an existing post. Only the post author can update their post."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Post updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to update this post",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/{postId}")
    public ResponseEntity<PostResource> updatePost(
            @Parameter(description = "ID of the post to update", example = "1")
            @PathVariable Long postId,
            @Parameter(description = "ID of the user requesting the update", example = "1")
            @RequestParam Long userId,
            @Valid @RequestBody UpdatePostResource resource
    ) {
        try {
            var command = new UpdatePostCommand(
                    postId,
                    userId,
                    resource.title(),
                    resource.body(),
                    resource.imageUrl()
            );

            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(toResource(post.get()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a post (logical deletion).
     */
    @Operation(
            summary = "Delete a post",
            description = "Performs logical deletion of a post. Only the post author can delete their post."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Post deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or post already deleted",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to delete this post",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResource> deletePost(
            @Parameter(description = "ID of the post to delete", example = "1")
            @PathVariable Long postId,
            @Parameter(description = "ID of the user requesting the deletion", example = "1")
            @RequestParam Long userId
    ) {
        try {
            var command = new DeletePostCommand(postId, userId);
            var post = postCommandService.handle(command);

            if (post.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(toResource(post.get()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
