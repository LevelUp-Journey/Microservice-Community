package com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects;

import java.util.List;

/**
 * Value object representing the content of a post.
 * Encapsulates text and images with business invariants.
 */
public record PostBody(
    String text,
    List<ImageRef> images
) {
    
    public PostBody {
        // Validate business invariants
        if ((text == null || text.trim().isEmpty()) && (images == null || images.isEmpty())) {
            throw new IllegalArgumentException("At least one of text or images must be provided");
        }
        
        if (text != null && text.length() > 500) {
            throw new IllegalArgumentException("Text cannot exceed 500 characters");
        }
        
        if (images != null && images.size() > 5) {
            throw new IllegalArgumentException("Cannot have more than 5 images");
        }
        
        // Ensure immutability - assign to the parameter, not reassign the field
        images = images != null ? List.copyOf(images) : List.of();
    }
    
    /**
     * Creates a text-only post body.
     */
    public static PostBody textOnly(String text) {
        return new PostBody(text, List.of());
    }
    
    /**
     * Creates an images-only post body.
     */
    public static PostBody imagesOnly(List<ImageRef> images) {
        return new PostBody(null, images);
    }
    
    /**
     * Checks if the post has text content.
     */
    public boolean hasText() {
        return text != null && !text.trim().isEmpty();
    }
    
    /**
     * Checks if the post has image content.
     */
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
}
