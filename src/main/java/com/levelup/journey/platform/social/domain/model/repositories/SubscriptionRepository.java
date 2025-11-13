package com.levelup.journey.platform.social.domain.model.repositories;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Subscription aggregate
 */
public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findById(SubscriptionId id);
    List<Subscription> findByUserId(UserId userId);
    List<Subscription> findByUserId(UserId userId, int page, int size);
    long countByUserId(UserId userId);
    List<Subscription> findByCommunityId(CommunityId communityId);
    Optional<Subscription> findByUserIdAndCommunityId(UserId userId, CommunityId communityId);
    void deleteById(SubscriptionId id);
}
