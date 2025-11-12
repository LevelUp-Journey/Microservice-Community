package com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.FollowEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Mongo repository for follow relationships.
 */
@Repository
public interface FollowMongoRepository extends MongoRepository<FollowEntity, String> {

    List<FollowEntity> findByFollowerId(String followerId);

    List<FollowEntity> findByFollowingId(String followingId);

    Optional<FollowEntity> findByFollowerIdAndFollowingId(String followerId, String followingId);

    long countByFollowingId(String followingId);
}
