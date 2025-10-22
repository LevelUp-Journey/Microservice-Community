package com.levelup.journey.platform.moderation.domain.services;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;

import java.util.List;

/**
 * Service for content analysis and banned word detection
 */
public interface ContentAnalysisService {
    
    /**
     * Analyze content for suspicious words
     * @param content text content to analyze
     * @return list of suspicious words found
     */
    List<String> findSuspiciousWords(String content);
    
    /**
     * Check if content is appropriate
     * @param content text content to check
     * @return true if content is clean
     */
    boolean isContentAppropriate(String content);
    
    /**
     * Categorize content based on suspicious words found
     * @param suspiciousWords list of suspicious words
     * @return appropriate report category
     */
    ReportCategory categorizeContent(List<String> suspiciousWords);
    
    /**
     * Analyze both title and content
     * @param title post title
     * @param content post content
     * @return list of all suspicious words found
     */
    List<String> analyzeContent(String title, String content);
}