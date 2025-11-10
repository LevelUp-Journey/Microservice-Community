package com.levelup.journey.platform.social.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Create Subscription Resource
 * Request payload for creating a new subscription
 */
public record CreateSubscriptionResource(
        @NotBlank(message = "El ID de la comunidad es obligatorio")
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "El ID de la comunidad debe ser un UUID válido")
        String communityId
) {
}
