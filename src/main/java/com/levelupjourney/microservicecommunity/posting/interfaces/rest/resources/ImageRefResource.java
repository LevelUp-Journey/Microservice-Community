package com.levelupjourney.microservicecommunity.posting.interfaces.rest.resources;

import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.ImageRef;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * REST resource representing an image reference in a post.
 */
@Schema(description = "Image reference in a post")
public record ImageRefResource(
    
    @Schema(description = "URL of the image", example = "https://cdn.example.com/image.jpg")
    @NotBlank(message = "Image URL cannot be blank")
    String url,
    
    @Schema(description = "Alternative text for accessibility", example = "Beautiful sunset")
    String altText
    
) {
    
    /**
     * Converts this resource to domain value object.
     */
    public ImageRef toDomain() {
        return new ImageRef(url, altText);
    }
    
    /**
     * Creates a resource from domain value object.
     */
    public static ImageRefResource fromDomain(ImageRef imageRef) {
        return new ImageRefResource(imageRef.url(), imageRef.altText());
    }
}
