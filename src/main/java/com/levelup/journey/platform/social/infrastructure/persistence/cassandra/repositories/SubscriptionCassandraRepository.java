package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.SubscriptionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.SubscriptionPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cassandra Repository for Subscription entities
 */
@Repository
public interface SubscriptionCassandraRepository extends CassandraRepository<SubscriptionEntity, SubscriptionPrimaryKey> {

    /**
     * Find all subscriptions by user ID
     * @param userId the user identifier
     * @return list of subscriptions
     */
    List<SubscriptionEntity> findById_UserId(UUID userId);

    /**
     * Find all subscriptions by community ID
     * @param communityId the community identifier
     * @return list of subscriptions
     */
    List<SubscriptionEntity> findById_CommunityId(UUID communityId);

    /**
     * Find subscription by user ID and community ID
     * @param userId the user identifier
     * @param communityId the community identifier
     * @return the subscription if found
     */
    @Query("SELECT * FROM subscriptions WHERE userId = ?0 AND communityId = ?1 ALLOW FILTERING")
    Optional<SubscriptionEntity> findByUserIdAndCommunityId(UUID userId, UUID communityId);
}
