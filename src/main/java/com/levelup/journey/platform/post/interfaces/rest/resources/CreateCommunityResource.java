package com.levelup.journey.platform.post.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * Create Community Resource
 * Request payload for creating a new community
 */
public record CreateCommunityResource(
        @NotBlank(message = "El nombre de la comunidad es obligatorio")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción debe tener máximo 500 caracteres")
        String description,

        @URL(message = "La URL de la imagen debe ser válida")
        String imageUrl
) {
}
