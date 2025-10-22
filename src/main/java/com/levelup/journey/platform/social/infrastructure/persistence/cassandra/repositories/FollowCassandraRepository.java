package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.FollowEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Cassandra Repository for Follow entities
 */
@Repository
public interface FollowCassandraRepository extends CassandraRepository<FollowEntity, String> {

    /**
     * Find all follows where the user is the follower
     * @param followerId the follower identifier
     * @return list of follow relationships
     */
    List<FollowEntity> findByFollowerId(String followerId);

    /**
     * Find all follows where the user is being followed
     * @param followingId the following identifier
     * @return list of follow relationships
     */
    List<FollowEntity> findByFollowingId(String followingId);

    /**
     * Find follow relationship by follower and following IDs
     * @param followerId the follower identifier
     * @param followingId the following identifier
     * @return the follow relationship if found
     */
    Optional<FollowEntity> findByFollowerIdAndFollowingId(String followerId, String followingId);
}
