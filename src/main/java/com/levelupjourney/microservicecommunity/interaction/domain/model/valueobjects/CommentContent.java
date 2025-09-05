package com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects;

/**
 * Value object representing the content of a comment.
 * Encapsulates comment text with business validation.
 */
public record CommentContent(String text) {
    
    public CommentContent {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be null or empty");
        }
        
        if (text.length() > 300) {
            throw new IllegalArgumentException("Comment text cannot exceed 300 characters");
        }
        
        // Trim whitespace
        text = text.trim();
    }
    
    /**
     * Gets the character count of the comment.
     */
    public int length() {
        return text.length();
    }
    
    /**
     * Checks if the comment is within the allowed length.
     */
    public boolean isValid() {
        return text.length() <= 300;
    }
}
