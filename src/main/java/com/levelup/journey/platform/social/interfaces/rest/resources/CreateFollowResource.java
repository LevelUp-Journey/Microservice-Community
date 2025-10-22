package com.levelup.journey.platform.social.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Create Follow Resource
 * Request payload for creating a new follow relationship
 */
public record CreateFollowResource(
        @NotBlank(message = "El ID de la relación de seguimiento es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID debe ser un UUID válido")
        String id,

        @NotBlank(message = "El ID del seguidor es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del seguidor debe ser un UUID válido")
        String followerId,

        @NotBlank(message = "El ID del usuario a seguir es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del usuario a seguir debe ser un UUID válido")
        String followingId
) {
}
