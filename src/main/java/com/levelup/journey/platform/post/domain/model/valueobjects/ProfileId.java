package com.levelup.journey.platform.post.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * ProfileId es un value object que representa el identificador único de un perfil de usuario.
 * Este ID proviene del microservicio Profile y es diferente del UserId.
 *
 * @param value El UUID del perfil como String
 */
public record ProfileId(String value) {

    public ProfileId {
        Objects.requireNonNull(value, "ProfileId no puede ser null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ProfileId no puede estar vacío");
        }
        validateUUID(value);
    }

    /**
     * Valida que el valor sea un UUID válido
     * @param value el valor a validar
     * @throws IllegalArgumentException si el valor no es un UUID válido
     */
    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ProfileId debe ser un UUID válido: " + value, e);
        }
    }

    /**
     * Crea un ProfileId a partir de un String
     * @param value el valor del UUID como String
     * @return una nueva instancia de ProfileId
     */
    public static ProfileId of(String value) {
        return new ProfileId(value);
    }

    /**
     * Genera un ProfileId aleatorio
     * @return una nueva instancia de ProfileId con un UUID aleatorio
     */
    public static ProfileId random() {
        return new ProfileId(UUID.randomUUID().toString());
    }
}
