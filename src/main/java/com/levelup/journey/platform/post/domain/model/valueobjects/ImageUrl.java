package com.levelup.journey.platform.post.domain.model.valueobjects;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Value object representing an image URL
 * Validates that the provided string is a valid HTTP/HTTPS URL link
 */
public record ImageUrl(String url) {

    public ImageUrl {
        if (url != null && !url.isBlank()) {
            validateUrl(url);
        }
    }

    /**
     * Validates that the string is a valid HTTP/HTTPS URL link
     * @param url the URL string to validate
     * @throws IllegalArgumentException if the URL is invalid or not a web link
     */
    private static void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();

            // Ensure it has a scheme
            if (scheme == null) {
                throw new IllegalArgumentException("Image URL must be a valid web link (http/https): " + url);
            }

            // Only allow HTTP and HTTPS schemes
            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                throw new IllegalArgumentException("Image URL must use HTTP or HTTPS protocol, got: " + scheme);
            }

            // Ensure it has a host
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new IllegalArgumentException("Image URL must have a valid host: " + url);
            }

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid image URL format: " + url, e);
        }
    }

    /**
     * Creates an ImageUrl from a string
     * @param url the URL string
     * @return ImageUrl instance
     */
    public static ImageUrl of(String url) {
        return new ImageUrl(url);
    }

    /**
     * Creates an empty ImageUrl (null value)
     * @return ImageUrl with null value
     */
    public static ImageUrl empty() {
        return new ImageUrl(null);
    }

    /**
     * Checks if the image URL is empty
     * @return true if URL is null or blank
     */
    public boolean isEmpty() {
        return url == null || url.isBlank();
    }
}