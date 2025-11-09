package com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.ReactionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Mongo repository for reactions.
 */
@Repository
public interface ReactionMongoRepository extends MongoRepository<ReactionEntity, String> {

    List<ReactionEntity> findByPostId(String postId);

    List<ReactionEntity> findByUserId(String userId);

    Optional<ReactionEntity> findByPostIdAndUserId(String postId, String userId);

    void deleteByPostIdAndUserId(String postId, String userId);
}
