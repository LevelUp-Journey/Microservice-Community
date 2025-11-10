package com.levelup.journey.platform.social.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Create Reaction Resource
 * Request payload for creating a new reaction
 */
public record CreateReactionResource(
        @NotBlank(message = "El ID del post es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del post debe ser un UUID válido")
        String postId,

        @NotBlank(message = "El tipo de reacción es obligatorio")
        @Pattern(regexp = "^LIKE$",
                message = "El tipo de reacción debe ser: LIKE")
        String reactionType
) {
}
