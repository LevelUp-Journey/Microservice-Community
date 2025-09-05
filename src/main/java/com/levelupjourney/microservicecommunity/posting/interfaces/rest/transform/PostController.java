package com.levelupjourney.microservicecommunity.posting.interfaces.rest.transform;

import com.levelupjourney.microservicecommunity.posting.domain.model.commands.CreatePostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.DeletePostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.EditPostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.queries.GetPostFeedQuery;
import com.levelupjourney.microservicecommunity.posting.domain.model.queries.GetPostQuery;
import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.PostBody;
import com.levelupjourney.microservicecommunity.posting.domain.services.PostCommandService;
import com.levelupjourney.microservicecommunity.posting.domain.services.PostQueryService;
import com.levelupjourney.microservicecommunity.posting.interfaces.rest.resources.CreatePostResource;
import com.levelupjourney.microservicecommunity.posting.interfaces.rest.resources.PostResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for post operations.
 * Handles HTTP requests and transforms between REST resources and domain commands/queries.
 */
@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Posts", description = "Post management operations")
public class PostController {
    
    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;
    
    public PostController(
        PostCommandService postCommandService,
        PostQueryService postQueryService
    ) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new post", description = "Creates a new post with the provided content")
    public ResponseEntity<PostResource> createPost(
        @Parameter(description = "ID of the user creating the post") @RequestHeader("X-User-Id") String authorId,
        @Valid @RequestBody CreatePostResource resource
    ) {
        var postBody = new PostBody(resource.text(), resource.toDomainImages());
        var command = new CreatePostCommand(authorId, postBody);
        
        var postId = postCommandService.handle(command)
            .orElseThrow(() -> new RuntimeException("Failed to create post"));
        
        // Fetch the created post to return it
        var post = postQueryService.handle(new GetPostQuery(postId.value()))
            .orElseThrow(() -> new RuntimeException("Failed to retrieve created post"));
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PostResource.fromDomain(post));
    }
    
    @GetMapping("/{postId}")
    @Operation(summary = "Get a post by ID", description = "Retrieves a specific post by its identifier")
    public ResponseEntity<PostResource> getPost(
        @Parameter(description = "Post identifier") @PathVariable String postId
    ) {
        var query = new GetPostQuery(postId);
        
        return postQueryService.handle(query)
            .map(post -> ResponseEntity.ok(PostResource.fromDomain(post)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get post feed", description = "Retrieves a paginated list of posts")
    public ResponseEntity<Page<PostResource>> getPostFeed(
        @Parameter(description = "Filter by author ID (optional)") @RequestParam(required = false) String authorId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        var query = authorId != null 
            ? GetPostFeedQuery.byAuthor(authorId, pageable)
            : GetPostFeedQuery.allPosts(pageable);
        
        var posts = postQueryService.handle(query)
            .map(PostResource::fromDomain);
        
        return ResponseEntity.ok(posts);
    }
    
    @PatchMapping("/{postId}")
    @Operation(summary = "Edit a post", description = "Updates the content of an existing post")
    public ResponseEntity<PostResource> editPost(
        @Parameter(description = "Post identifier") @PathVariable String postId,
        @Parameter(description = "ID of the user editing the post") @RequestHeader("X-User-Id") String editorId,
        @Valid @RequestBody CreatePostResource resource
    ) {
        var postBody = new PostBody(resource.text(), resource.toDomainImages());
        var command = new EditPostCommand(postId, editorId, postBody);
        
        var updatedPostId = postCommandService.handle(command)
            .orElseThrow(() -> new RuntimeException("Failed to edit post"));
        
        // Fetch the updated post to return it
        var post = postQueryService.handle(new GetPostQuery(updatedPostId.value()))
            .orElseThrow(() -> new RuntimeException("Failed to retrieve edited post"));
        
        return ResponseEntity.ok(PostResource.fromDomain(post));
    }
    
    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete a post", description = "Soft deletes a post")
    public ResponseEntity<Void> deletePost(
        @Parameter(description = "Post identifier") @PathVariable String postId,
        @Parameter(description = "ID of the user deleting the post") @RequestHeader("X-User-Id") String deleterId
    ) {
        var command = new DeletePostCommand(postId, deleterId);
        
        postCommandService.handle(command)
            .orElseThrow(() -> new RuntimeException("Failed to delete post"));
        
        return ResponseEntity.noContent().build();
    }
}
