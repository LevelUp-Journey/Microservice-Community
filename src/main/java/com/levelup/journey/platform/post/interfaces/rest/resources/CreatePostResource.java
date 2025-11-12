package com.levelup.journey.platform.post.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Create Post Resource
 * Request payload for creating a new post
 */
public record CreatePostResource(
        @NotBlank(message = "El ID de la comunidad es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID de la comunidad debe ser un UUID válido")
        String communityId,

        @NotBlank(message = "El contenido es obligatorio")
        @Size(min = 1, max = 10000, message = "El contenido debe tener entre 1 y 10000 caracteres")
        String content,

        @JsonProperty(value = "imageUrl", required = false)
        Object imageUrl
) {
}
