package com.levelup.journey.platform.user.application.acl;

import com.levelup.journey.platform.user.domain.model.queries.GetUserByUserIdQuery;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.user.domain.services.UserQueryService;
import com.levelup.journey.platform.user.interfaces.acl.UserContextFacade;
import org.springframework.stereotype.Service;

/**
 * ACL Facade Implementation for User Context
 * Provides access to user data for other bounded contexts
 */
@Service
public class UserContextFacadeImpl implements UserContextFacade {

    private final UserQueryService userQueryService;

    public UserContextFacadeImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public String getUsernameByUserId(String userId) {
        try {
            var query = new GetUserByUserIdQuery(UserId.of(userId));
            var user = userQueryService.handle(query);
            return user.map(u -> u.getUsername()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getProfileUrlByUserId(String userId) {
        try {
            var query = new GetUserByUserIdQuery(UserId.of(userId));
            var user = userQueryService.handle(query);
            return user.map(u -> u.getProfileUrl()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}