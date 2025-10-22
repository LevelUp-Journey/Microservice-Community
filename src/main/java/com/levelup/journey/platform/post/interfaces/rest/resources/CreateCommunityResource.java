package com.levelup.journey.platform.post.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Create Community Resource
 * Request payload for creating a new community
 */
public record CreateCommunityResource(
        @NotBlank(message = "El ID de la comunidad es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID debe ser un UUID válido")
        String id,

        @NotBlank(message = "El ID del propietario es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del propietario debe ser un UUID válido")
        String ownerId,

        @NotBlank(message = "El nombre de la comunidad es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.,()]+$", message = "El nombre contiene caracteres no permitidos")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.,()!?;:]+$", message = "La descripción contiene caracteres no permitidos")
        String description
) {
}
