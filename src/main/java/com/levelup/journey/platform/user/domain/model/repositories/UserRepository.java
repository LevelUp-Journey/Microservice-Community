package com.levelup.journey.platform.user.domain.model.repositories;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Repository interface for User aggregate
 */
public interface UserRepository {

    /**
     * Saves a user
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Finds a user by user ID
     * @param userId the user ID to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUserId(UserId userId);

    /**
     * Finds a user by profile ID
     * @param profileId the profile ID to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByProfileId(ProfileId profileId);

    /**
     * Checks if a user exists by user ID
     * @param userId the user ID to check
     * @return true if the user exists, false otherwise
     */
    boolean existsByUserId(UserId userId);
}
