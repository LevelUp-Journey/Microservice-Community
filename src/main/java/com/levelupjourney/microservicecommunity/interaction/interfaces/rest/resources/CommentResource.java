package com.levelupjourney.microservicecommunity.interaction.interfaces.rest.resources;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * REST resource representing a comment in API responses.
 */
@Schema(description = "Comment information")
public record CommentResource(
    
    @Schema(description = "Unique identifier of the comment")
    String id,
    
    @Schema(description = "ID of the post this comment belongs to")
    String postId,
    
    @Schema(description = "ID of the comment author")
    String authorId,
    
    @Schema(description = "Text content of the comment")
    String text,
    
    @Schema(description = "Comment creation timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Comment last edit timestamp")
    LocalDateTime editedAt,
    
    @Schema(description = "Whether the comment has been edited")
    boolean isEdited
    
) {
    
    /**
     * Creates a CommentResource from a domain Comment entity.
     */
    public static CommentResource fromDomain(Comment comment) {
        return new CommentResource(
            comment.getCommentId().value(),
            comment.getPostId().value(),
            comment.getAuthorId().value(),
            comment.getContent().text(),
            comment.getCreatedAt(),
            comment.getEditedAt(),
            comment.isEdited()
        );
    }
}
