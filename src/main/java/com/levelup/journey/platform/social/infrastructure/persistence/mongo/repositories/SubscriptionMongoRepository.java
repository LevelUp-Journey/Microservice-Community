package com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.SubscriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Mongo repository for subscriptions.
 */
@Repository
public interface SubscriptionMongoRepository extends MongoRepository<SubscriptionEntity, String> {

    List<SubscriptionEntity> findByUserId(String userId);

    List<SubscriptionEntity> findByUserId(String userId, Pageable pageable);

    long countByUserId(String userId);

    List<SubscriptionEntity> findByCommunityId(String communityId);

    Optional<SubscriptionEntity> findByUserIdAndCommunityId(String userId, String communityId);

    void deleteByUserIdAndCommunityId(String userId, String communityId);
}
