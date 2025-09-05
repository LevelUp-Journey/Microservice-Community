package com.levelupjourney.microservicecommunity.interaction.interfaces.rest.resources;

import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST resource for adding a new comment.
 */
@Schema(description = "Request to add a new comment")
public record AddCommentResource(
    
    @Schema(description = "Text content of the comment", example = "Great post!")
    @NotBlank(message = "Comment text cannot be blank")
    @Size(max = 300, message = "Comment cannot exceed 300 characters")
    String text
    
) {
    
    /**
     * Converts this resource to domain value object.
     */
    public CommentContent toDomainContent() {
        return new CommentContent(text);
    }
}
