package com.levelupjourney.microservicecommunity.interaction.interfaces.rest.transform;

import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.AddCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.DeleteCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.EditCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.services.CommentCommandService;
import com.levelupjourney.microservicecommunity.interaction.domain.services.CommentQueryService;
import com.levelupjourney.microservicecommunity.interaction.interfaces.rest.resources.AddCommentResource;
import com.levelupjourney.microservicecommunity.interaction.interfaces.rest.resources.CommentResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for comment operations.
 */
@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "Comment management operations")
public class CommentController {
    
    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;
    
    public CommentController(CommentCommandService commentCommandService,
                           CommentQueryService commentQueryService) {
        this.commentCommandService = commentCommandService;
        this.commentQueryService = commentQueryService;
    }
    
    @PostMapping("/posts/{postId}")
    @Operation(summary = "Add comment to post", description = "Creates a new comment on the specified post")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<Map<String, String>> addComment(
        @Parameter(description = "ID of the post to comment on") @PathVariable String postId,
        @Parameter(description = "ID of the user creating the comment") @RequestHeader("X-User-Id") String authorId,
        @Valid @RequestBody AddCommentResource resource
    ) {
        var command = new AddCommentCommand(postId, authorId, resource.text());
        
        var commentId = commentCommandService.handle(command)
            .orElseThrow(() -> new RuntimeException("Failed to create comment"));
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "commentId", commentId.value(),
                "postId", postId,
                "message", "Comment added successfully"
            ));
    }
    
    @PutMapping("/{commentId}")
    @Operation(summary = "Edit comment", description = "Updates the content of an existing comment")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "403", description = "Not authorized to edit this comment")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<Map<String, String>> editComment(
        @Parameter(description = "ID of the comment to edit") @PathVariable String commentId,
        @Parameter(description = "ID of the user editing the comment") @RequestHeader("X-User-Id") String authorId,
        @Valid @RequestBody AddCommentResource resource
    ) {
        var command = new EditCommentCommand(commentId, authorId, resource.text());
        
        var updatedCommentId = commentCommandService.handle(command)
            .orElseThrow(() -> new RuntimeException("Failed to edit comment"));
        
        return ResponseEntity.ok(Map.of(
            "commentId", updatedCommentId.value(),
            "message", "Comment updated successfully"
        ));
    }
    
    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Marks a comment as deleted")
    @ApiResponse(responseCode = "200", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "403", description = "Not authorized to delete this comment")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<Map<String, String>> deleteComment(
        @Parameter(description = "ID of the comment to delete") @PathVariable String commentId,
        @Parameter(description = "ID of the user deleting the comment") @RequestHeader("X-User-Id") String authorId
    ) {
        var command = new DeleteCommentCommand(commentId, authorId);
        
        var deletedCommentId = commentCommandService.handle(command)
            .orElseThrow(() -> new RuntimeException("Failed to delete comment"));
        
        return ResponseEntity.ok(Map.of(
            "commentId", deletedCommentId.value(),
            "message", "Comment deleted successfully"
        ));
    }
    
    @GetMapping("/posts/{postId}")
    @Operation(summary = "Get comments for post", description = "Retrieves all comments for the specified post")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<List<CommentResource>> getCommentsForPost(
        @Parameter(description = "ID of the post to get comments for") @PathVariable String postId
    ) {
        var comments = commentQueryService.findByPostId(postId);
        
        var commentResources = comments.stream()
            .map(CommentResource::fromDomain)
            .toList();
        
        return ResponseEntity.ok(commentResources);
    }
}
