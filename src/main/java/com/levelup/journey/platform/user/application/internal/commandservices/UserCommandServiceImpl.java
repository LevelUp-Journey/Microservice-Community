package com.levelup.journey.platform.user.application.internal.commandservices;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.commands.RegisterUserCommand;
import com.levelup.journey.platform.user.domain.model.commands.UpdateUserProfileCommand;
import com.levelup.journey.platform.user.domain.model.repositories.UserRepository;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.user.domain.services.UserCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of UserCommandService
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger logger = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    private final UserRepository userRepository;

    public UserCommandServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Optional<User> handle(RegisterUserCommand command) {
        try {
            UserId userId = UserId.of(command.userId());
            ProfileId profileId = ProfileId.of(command.profileId());

            // Check if user already exists
            if (userRepository.existsByUserId(userId)) {
                logger.warn("User registration attempted for existing userId: {}", userId);
                return userRepository.findByUserId(userId)
                        .map(existing -> {
                            User updated = existing.updateProfile(command.username(), command.profileUrl(), command.occurredOn());
                            userRepository.save(updated);
                            logger.info("Refreshed cached profile data during registration replay for userId={}", userId);
                            return updated;
                        });
            }

            // Register the new user
            User user = User.register(
                    userId,
                    profileId,
                    command.username(),
                    command.profileUrl(),
                    command.occurredOn());
            User savedUser = userRepository.save(user);

            logger.info("User registered successfully: userId={}, profileId={}", userId, profileId);
            return Optional.of(savedUser);

        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<User> handle(UpdateUserProfileCommand command) {
        try {
            UserId userId = UserId.of(command.userId());
            ProfileId profileId = ProfileId.of(command.profileId());

            Optional<User> userOptional = userRepository.findByUserId(userId);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByProfileId(profileId);
            }

            if (userOptional.isEmpty()) {
                logger.warn("Profile update received for unknown userId={} profileId={}", userId, profileId);
                return Optional.empty();
            }

            User updatedUser = userOptional.get()
                    .updateProfile(command.username(), command.profileUrl(), command.occurredOn());
            userRepository.save(updatedUser);

            logger.info("User profile updated successfully: userId={}, profileId={}", userId, profileId);
            return Optional.of(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user profile: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
