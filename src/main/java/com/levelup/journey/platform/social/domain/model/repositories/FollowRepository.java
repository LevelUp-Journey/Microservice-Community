package com.levelup.journey.platform.social.domain.model.repositories;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Follow aggregate
 */
public interface FollowRepository {
    Follow save(Follow follow);
    Optional<Follow> findById(FollowId id);
    List<Follow> findByFollowerId(UserId followerId);
    List<Follow> findByFollowingId(UserId followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(UserId followerId, UserId followingId);
    long countByFollowingId(UserId followingId);
    void deleteById(FollowId id);
}
