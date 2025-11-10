package com.levelup.journey.platform.user.domain.model.queries;

import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;

/**
 * Query to retrieve a user by their profile ID.
 *
 * @param profileId profile identifier belonging to the Profile bounded context
 */
public record GetUserByProfileIdQuery(ProfileId profileId) {
    public GetUserByProfileIdQuery {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
    }
}
