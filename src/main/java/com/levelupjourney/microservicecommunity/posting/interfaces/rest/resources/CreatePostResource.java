package com.levelupjourney.microservicecommunity.posting.interfaces.rest.resources;

import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.ImageRef;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * REST resource for creating a new post.
 */
@Schema(description = "Request to create a new post")
public record CreatePostResource(
    
    @Schema(description = "Text content of the post", example = "Hello world!")
    @Size(max = 500, message = "Text cannot exceed 500 characters")
    String text,
    
    @Schema(description = "List of images attached to the post")
    @Valid
    @Size(max = 5, message = "Cannot have more than 5 images")
    List<ImageRefResource> images
    
) {
    
    public CreatePostResource {
        // Validate at least one content type
        boolean hasText = text != null && !text.trim().isEmpty();
        boolean hasImages = images != null && !images.isEmpty();
        
        if (!hasText && !hasImages) {
            throw new IllegalArgumentException("At least one of text or images must be provided");
        }
        
        // Ensure immutability - assign to parameter, not reassign field
        images = images != null ? List.copyOf(images) : List.of();
    }
    
    /**
     * Converts this resource to domain value objects.
     */
    public List<ImageRef> toDomainImages() {
        return images.stream()
            .map(ImageRefResource::toDomain)
            .toList();
    }
}
