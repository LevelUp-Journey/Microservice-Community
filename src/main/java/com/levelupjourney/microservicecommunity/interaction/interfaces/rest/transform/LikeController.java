package com.levelupjourney.microservicecommunity.interaction.interfaces.rest.transform;

import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.LikePostCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.UnlikePostCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.services.LikeCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for like operations.
 */
@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Likes", description = "Post like management operations")
public class LikeController {
    
    private final LikeCommandService likeCommandService;
    
    public LikeController(LikeCommandService likeCommandService) {
        this.likeCommandService = likeCommandService;
    }
    
    @PostMapping("/{postId}/like")
    @Operation(summary = "Like a post", description = "Adds a like to the specified post")
    @ApiResponse(responseCode = "200", description = "Post liked successfully")
    @ApiResponse(responseCode = "409", description = "Post already liked by user")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<Map<String, Object>> likePost(
        @Parameter(description = "ID of the post to like") @PathVariable String postId,
        @Parameter(description = "ID of the user liking the post") @RequestHeader("X-User-Id") String userId
    ) {
        var command = new LikePostCommand(postId, userId);
        
        boolean success = likeCommandService.handle(command);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "postId", postId,
                "userId", userId,
                "liked", true,
                "message", "Post liked successfully"
            ));
        } else {
            return ResponseEntity.status(409).body(Map.of(
                "postId", postId,
                "userId", userId,
                "liked", true,
                "message", "Post already liked by user"
            ));
        }
    }
    
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "Unlike a post", description = "Removes a like from the specified post")
    @ApiResponse(responseCode = "200", description = "Post unliked successfully")
    @ApiResponse(responseCode = "404", description = "Post not found or not previously liked")
    public ResponseEntity<Map<String, Object>> unlikePost(
        @Parameter(description = "ID of the post to unlike") @PathVariable String postId,
        @Parameter(description = "ID of the user unliking the post") @RequestHeader("X-User-Id") String userId
    ) {
        var command = new UnlikePostCommand(postId, userId);
        
        boolean success = likeCommandService.handle(command);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "postId", postId,
                "userId", userId,
                "liked", false,
                "message", "Post unliked successfully"
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of(
                "postId", postId,
                "userId", userId,
                "liked", false,
                "message", "Post was not previously liked by user"
            ));
        }
    }
}
