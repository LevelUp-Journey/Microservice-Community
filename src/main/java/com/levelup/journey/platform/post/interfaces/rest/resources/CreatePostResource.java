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

        @NotBlank(message = "El ID del autor es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del autor debe ser un UUID válido")
        String authorId,

        @NotBlank(message = "El ID del perfil del autor es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del perfil del autor debe ser un UUID válido")
        String authorProfileId,

        @NotBlank(message = "El título es obligatorio")
        @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
        String title,

        @NotBlank(message = "El contenido es obligatorio")
        @Size(min = 1, max = 5000, message = "El contenido debe tener entre 1 y 5000 caracteres")
        String content,

        @JsonProperty(value = "imageUrl", required = false)
        Object imageUrl
) {
}
