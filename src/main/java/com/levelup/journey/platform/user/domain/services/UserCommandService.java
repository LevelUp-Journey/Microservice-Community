package com.levelup.journey.platform.user.domain.services;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.commands.RegisterUserCommand;
import com.levelup.journey.platform.user.domain.model.commands.UpdateUserProfileCommand;

import java.util.Optional;

/**
 * Command service interface for User operations
 */
public interface UserCommandService {

    /**
     * Handles the registration of a new user
     * @param command the registration command
     * @return an Optional containing the registered user
     */
    Optional<User> handle(RegisterUserCommand command);

    /**
     * Handles updates coming from the profile bounded context so cached data stays in sync.
     * @param command update command with the latest profile snapshot
     * @return the updated user if it exists
     */
    Optional<User> handle(UpdateUserProfileCommand command);
}
