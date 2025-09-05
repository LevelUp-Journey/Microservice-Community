package com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects;

/**
 * Value object representing an image reference in a post.
 * Contains the image URL and optional alt text for accessibility.
 */
public record ImageRef(
    String url,
    String altText
) {
    
    public ImageRef {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        
        // Basic URL validation
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Image URL must be a valid HTTP/HTTPS URL");
        }
    }
    
    /**
     * Creates an image reference without alt text.
     */
    public static ImageRef of(String url) {
        return new ImageRef(url, null);
    }
    
    /**
     * Creates an image reference with alt text.
     */
    public static ImageRef withAltText(String url, String altText) {
        return new ImageRef(url, altText);
    }
    
    /**
     * Checks if the image has alt text.
     */
    public boolean hasAltText() {
        return altText != null && !altText.trim().isEmpty();
    }
}
