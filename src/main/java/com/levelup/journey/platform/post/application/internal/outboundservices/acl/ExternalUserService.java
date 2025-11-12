package com.levelup.journey.platform.post.application.internal.outboundservices.acl;

import com.levelup.journey.platform.user.interfaces.acl.UserContextFacade;
import org.springframework.stereotype.Service;

/**
 * External User Service
 * ACL implementation for accessing user data from external context
 */
@Service
public class ExternalUserService {

    private final UserContextFacade userContextFacade;

    public ExternalUserService(UserContextFacade userContextFacade) {
        this.userContextFacade = userContextFacade;
    }

    /**
     * Fetch username by user ID
     * @param userId The user ID
     * @return The username, or null if not found
     */
    public String fetchUsernameByUserId(String userId) {
        return userContextFacade.getUsernameByUserId(userId);
    }

    /**
     * Fetch profile URL by user ID
     * @param userId The user ID
     * @return The profile URL, or null if not found
     */
    public String fetchProfileUrlByUserId(String userId) {
        return userContextFacade.getProfileUrlByUserId(userId);
    }
}