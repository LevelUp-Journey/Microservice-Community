package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.SubscriptionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Cassandra Repository for Subscription entities
 */
@Repository
public interface SubscriptionCassandraRepository extends CassandraRepository<SubscriptionEntity, String> {

    /**
     * Find all subscriptions by user ID
     * @param userId the user identifier
     * @return list of subscriptions
     */
    List<SubscriptionEntity> findByUserId(String userId);

    /**
     * Find all subscriptions by community ID
     * @param communityId the community identifier
     * @return list of subscriptions
     */
    List<SubscriptionEntity> findByCommunityId(String communityId);

    /**
     * Find subscription by user ID and community ID
     * @param userId the user identifier
     * @param communityId the community identifier
     * @return the subscription if found
     */
    Optional<SubscriptionEntity> findByUserIdAndCommunityId(String userId, String communityId);
}
