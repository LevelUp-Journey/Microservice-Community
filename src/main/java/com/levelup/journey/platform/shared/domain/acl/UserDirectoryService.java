package com.levelup.journey.platform.shared.domain.acl;

/**
 * Anti-Corruption Layer contract to validate user identities from the User bounded context.
 */
public interface UserDirectoryService {

    /**
     * Checks if a user exists in the User bounded context.
     * @param userId UUID of the user to validate
     * @return true when the user exists, false otherwise
     */
    boolean userExists(String userId);

    /**
     * Checks if a profile exists in the User bounded context.
     * @param profileId UUID of the profile to validate
     * @return true when the profile exists, false otherwise
     */
    boolean profileExists(String profileId);

    /**
     * Verifies that the provided user owns the provided profile.
     * @param userId UUID of the authenticated user
     * @param profileId UUID of the profile that should belong to the user
     * @return true when both exist and belong together, false otherwise
     */
    boolean userMatchesProfile(String userId, String profileId);

    /**
     * Retrieves the profile ID associated with the provided user ID.
     * @param userId UUID of the user
     * @return optional profile ID when the user exists
     */
    java.util.Optional<String> findProfileIdByUserId(String userId);

    /**
     * Convenience method that enforces the existence of a profile for the user.
     * @param userId UUID of the user
     * @return profile ID
     */
    default String requireProfileIdByUserId(String userId) {
        return findProfileIdByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un perfil para el usuario: " + userId));
    }
}
