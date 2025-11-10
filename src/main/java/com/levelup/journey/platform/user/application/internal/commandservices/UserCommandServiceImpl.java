package com.levelup.journey.platform.user.application.internal.commandservices;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.commands.RegisterUserCommand;
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
                return userRepository.findByUserId(userId);
            }

            // Register the new user
            User user = User.register(userId, profileId, command.occurredOn());
            User savedUser = userRepository.save(user);

            logger.info("User registered successfully: userId={}, profileId={}", userId, profileId);
            return Optional.of(savedUser);

        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
