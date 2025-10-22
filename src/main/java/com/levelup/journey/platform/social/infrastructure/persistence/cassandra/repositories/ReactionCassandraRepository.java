package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.ReactionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Cassandra Repository for Reaction entities
 */
@Repository
public interface ReactionCassandraRepository extends CassandraRepository<ReactionEntity, String> {

    /**
     * Find all reactions by post ID
     * @param postId the post identifier
     * @return list of reactions
     */
    List<ReactionEntity> findByPostId(String postId);

    /**
     * Find all reactions by user ID
     * @param userId the user identifier
     * @return list of reactions
     */
    List<ReactionEntity> findByUserId(String userId);

    /**
     * Find reaction by post ID and user ID
     * @param postId the post identifier
     * @param userId the user identifier
     * @return the reaction if found
     */
    Optional<ReactionEntity> findByPostIdAndUserId(String postId, String userId);
}
