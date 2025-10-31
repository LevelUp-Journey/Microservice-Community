package com.levelup.journey.platform.post.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Edit Comment Resource
 * Request payload for editing a comment
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EditCommentResource(
        @NotBlank(message = "El contenido del comentario es obligatorio")
        @Size(min = 1, max = 1000, message = "El comentario debe tener entre 1 y 1000 caracteres")
        String content,

        @JsonProperty(value = "imageUrl", required = false)
        Object imageUrl
) {
}