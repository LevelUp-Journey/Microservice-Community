package com.levelup.journey.platform.shared.domain.acl;

/**
 * Anti-Corruption Layer interface for content moderation services.
 * This interface defines the contract for cross-context communication
 * between the Post context and the Moderation context.
 */
public interface ContentModerationService {

    /**
     * Analyzes content for potential violations and creates reports if needed.
     *
     * @param contentId The ID of the content to analyze (post ID, comment ID, etc.)
     * @param authorId The ID of the content author
     * @param content The actual content text to analyze
     * @return The report ID if a violation was detected, null otherwise
     */
    String analyzeContent(String contentId, String authorId, String content);
}