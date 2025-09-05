package com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects;

import com.levelupjourney.microservicecommunity.shared.domain.validation.ValidationUtils;

/**
 * Value object representing the content of a comment.
 * Encapsulates comment text with business validation.
 */
public record CommentContent(String text) {
    
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 300;
    
    public CommentContent {
        ValidationUtils.requireLengthRange(text, MIN_LENGTH, MAX_LENGTH, "Comment text");
        
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
