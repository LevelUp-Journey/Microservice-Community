package com.levelup.journey.platform.post.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Create Post Resource
 * Request payload for creating a new post
 */
public record CreatePostResource(
        @NotBlank(message = "El ID del post es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del post debe ser un UUID válido")
        String id,

        @NotBlank(message = "El ID de la comunidad es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID de la comunidad debe ser un UUID válido")
        String communityId,

        @NotBlank(message = "El ID del autor es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del autor debe ser un UUID válido")
        String authorId,

        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.,()!?;:]+$", message = "El título contiene caracteres no permitidos")
        String title,

        @NotBlank(message = "El contenido es obligatorio")
        @Size(min = 10, max = 5000, message = "El contenido debe tener entre 10 y 5000 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.,()!?;:@#$%&*+=\\n\\r\\t]+$", message = "El contenido contiene caracteres no permitidos")
        String content
) {
}
