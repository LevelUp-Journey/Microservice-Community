package com.levelup.journey.platform.shared.infrastructure.acl;

import com.levelup.journey.platform.shared.domain.acl.UserDirectoryService;
import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.queries.GetUserByProfileIdQuery;
import com.levelup.journey.platform.user.domain.model.queries.GetUserByUserIdQuery;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.user.domain.services.UserQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ACL Adapter that exposes the User bounded context to other contexts through a safe contract.
 */
@Service
public class UserDirectoryServiceAclAdapter implements UserDirectoryService {

    private static final Logger logger = LoggerFactory.getLogger(UserDirectoryServiceAclAdapter.class);

    private final UserQueryService userQueryService;

    public UserDirectoryServiceAclAdapter(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public boolean userExists(String userId) {
        return findByUserId(userId).isPresent();
    }

    @Override
    public boolean profileExists(String profileId) {
        return findByProfileId(profileId).isPresent();
    }

    @Override
    public boolean userMatchesProfile(String userId, String profileId) {
        if (profileId == null || profileId.isBlank()) {
            return false;
        }
        return findByUserId(userId)
                .map(user -> user.getProfileId().value().equals(profileId))
                .orElse(false);
    }

    @Override
    public Optional<String> findProfileIdByUserId(String userId) {
        return findByUserId(userId)
                .map(user -> user.getProfileId().value());
    }

    private Optional<User> findByUserId(String rawUserId) {
        try {
            var query = new GetUserByUserIdQuery(UserId.of(rawUserId));
            return userQueryService.handle(query);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid userId received through ACL validation: {}", rawUserId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Unexpected error validating userId {}: {}", rawUserId, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<User> findByProfileId(String rawProfileId) {
        try {
            var query = new GetUserByProfileIdQuery(ProfileId.of(rawProfileId));
            return userQueryService.handle(query);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid profileId received through ACL validation: {}", rawProfileId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Unexpected error validating profileId {}: {}", rawProfileId, e.getMessage());
            return Optional.empty();
        }
    }
}
