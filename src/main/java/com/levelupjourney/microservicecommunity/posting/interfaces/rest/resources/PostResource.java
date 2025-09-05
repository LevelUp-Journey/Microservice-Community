package com.levelupjourney.microservicecommunity.posting.interfaces.rest.resources;

import com.levelupjourney.microservicecommunity.posting.domain.model.aggregates.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST resource representing a post in API responses.
 */
@Schema(description = "Post information")
public record PostResource(
    
    @Schema(description = "Unique identifier of the post")
    String id,
    
    @Schema(description = "ID of the post author")
    String authorId,
    
    @Schema(description = "Text content of the post")
    String text,
    
    @Schema(description = "List of images in the post")
    List<ImageRefResource> images,
    
    @Schema(description = "Post creation timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Post last edit timestamp")
    LocalDateTime editedAt,
    
    @Schema(description = "Whether the post has been edited")
    boolean isEdited
    
) {
    
    /**
     * Creates a PostResource from a domain Post entity.
     */
    public static PostResource fromDomain(Post post) {
        var images = post.getContent().images().stream()
            .map(ImageRefResource::fromDomain)
            .toList();
        
        return new PostResource(
            post.getPostId().value(),
            post.getAuthorId().value(),
            post.getContent().text(),
            images,
            post.getCreatedAt(),
            post.getEditedAt(),
            post.isEdited()
        );
    }
}
