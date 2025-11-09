package com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.FollowEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cassandra Repository for Follow entities
 */
@Repository
public interface FollowCassandraRepository extends CassandraRepository<FollowEntity, UUID> {

    /**
     * Find all follows where the user is the follower
     * @param followerId the follower identifier
     * @return list of follow relationships
     */
    List<FollowEntity> findByFollowerId(UUID followerId);

    /**
     * Find all follows where the user is being followed
     * @param followingId the following identifier
     * @return list of follow relationships
     */
    List<FollowEntity> findByFollowingId(UUID followingId);

    /**
     * Find follow relationship by follower and following IDs
     * @param followerId the follower identifier
     * @param followingId the following identifier
     * @return the follow relationship if found
     */
    @Query("SELECT * FROM follows WHERE followerId = ?0 AND followingId = ?1 ALLOW FILTERING")
    Optional<FollowEntity> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
}
