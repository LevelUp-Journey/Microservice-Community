package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.ReactionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.ReactionPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cassandra Repository for Reaction entities
 */
@Repository
public interface ReactionCassandraRepository extends CassandraRepository<ReactionEntity, ReactionPrimaryKey> {

    /**
     * Find all reactions by post ID
     * @param postId the post identifier
     * @return list of reactions
     */
    List<ReactionEntity> findById_PostId(UUID postId);

    /**
     * Find all reactions by user ID
     * @param userId the user identifier
     * @return list of reactions
     */
    List<ReactionEntity> findById_UserId(UUID userId);

    /**
     * Find reaction by post ID and user ID
     * @param postId the post identifier
     * @param userId the user identifier
     * @return the reaction if found
     */
    @Query("SELECT * FROM reactions WHERE postId = ?0 AND userId = ?1 ALLOW FILTERING")
    Optional<ReactionEntity> findByPostIdAndUserId(UUID postId, UUID userId);
}
