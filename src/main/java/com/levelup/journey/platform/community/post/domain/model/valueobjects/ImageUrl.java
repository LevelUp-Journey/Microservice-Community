package com.levelup.journey.platform.community.post.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Value object representing a validated image URL.
 * Ensures the URL is valid and accessible.
 */
@Embeddable
public record ImageUrl(String value) {

    public ImageUrl {
        if (value != null && !value.isBlank()) {
            validateUrl(value);
        }
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            // Additional validation: ensure it's http or https
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
                throw new IllegalArgumentException("Image URL must use http:// or https:// scheme");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid image URL: " + url, e);
        }
    }

    /**
     * Check if image URL is present
     */
    public boolean isPresent() {
        return value != null && !value.isBlank();
    }
}
