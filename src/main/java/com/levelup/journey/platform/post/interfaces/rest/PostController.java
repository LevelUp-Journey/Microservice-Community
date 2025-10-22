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
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<PostResource> createPost(@RequestBody CreatePostResource resource) {
        var command = CreatePostCommandFromResourceAssembler.toCommandFromResource(resource);
        var post = postCommandService.handle(command);

        if (post.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
        return new ResponseEntity<>(postResource, HttpStatus.CREATED);
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
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content)
    })
    public ResponseEntity<PostResource> getPostById(@PathVariable String postId) {
        var query = new GetPostByIdQuery(PostId.of(postId));
        var post = postQueryService.handle(query);

        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
        return ResponseEntity.ok(postResource);
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
        var query = new GetAllPostsQuery();
        var posts = postQueryService.handle(query);

        var postResources = posts.stream()
                .map(PostResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(postResources);
    }

    /**
     * Get posts by community ID
     */
    @GetMapping("/community/{communityId}")
    @Operation(summary = "Get posts by community", description = "Retrieve all posts from a specific community")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResource.class)))
    })
    public ResponseEntity<List<PostResource>> getPostsByCommunity(@PathVariable String communityId) {
        var query = new GetPostsByCommunityIdQuery(CommunityId.of(communityId));
        var posts = postQueryService.handle(query);

        var postResources = posts.stream()
                .map(PostResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(postResources);
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
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content)
    })
    public ResponseEntity<PostResource> addComment(@PathVariable String postId,
                                                   @RequestBody AddCommentResource resource) {
        var command = AddCommentCommandFromResourceAssembler.toCommandFromResource(postId, resource);
        var post = postCommandService.handle(command);

        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var postResource = PostResourceFromEntityAssembler.toResourceFromEntity(post.get());
        return ResponseEntity.ok(postResource);
    }
}
