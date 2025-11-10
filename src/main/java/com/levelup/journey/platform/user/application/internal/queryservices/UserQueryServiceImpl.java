package com.levelup.journey.platform.user.application.internal.queryservices;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.queries.GetUserByUserIdQuery;
import com.levelup.journey.platform.user.domain.model.repositories.UserRepository;
import com.levelup.journey.platform.user.domain.services.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of UserQueryService
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> handle(GetUserByUserIdQuery query) {
        return userRepository.findByUserId(query.userId());
    }
}
