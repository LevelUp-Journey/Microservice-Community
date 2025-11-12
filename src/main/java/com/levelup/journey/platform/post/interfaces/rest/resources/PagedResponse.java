package com.levelup.journey.platform.post.interfaces.rest.resources;

import java.util.List;

/**
 * Paged Response Resource
 * Generic wrapper for paginated API responses with metadata
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> PagedResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return new PagedResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious
        );
    }
}
