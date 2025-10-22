package com.levelup.journey.platform.post.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Add Comment Resource
 * Request payload for adding a comment to a post
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddCommentResource(
        @NotBlank(message = "El ID del autor es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID del autor debe ser un UUID válido")
        String authorId,

        @NotBlank(message = "El contenido del comentario es obligatorio")
        @Size(min = 1, max = 1000, message = "El comentario debe tener entre 1 y 1000 caracteres")
        String content,

        @JsonProperty(value = "imageUrl", required = false)
        Object imageUrl
) {
}
