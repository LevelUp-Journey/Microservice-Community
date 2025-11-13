package com.levelup.journey.platform.social.application.internal.queryservices;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByCommunityIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.services.SubscriptionQueryService;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Subscription Query Service Implementation
 * Handles queries for Subscription aggregate
 */
@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        return subscriptionRepository.findById(query.id());
    }

    @Override
    public List<Subscription> handle(GetSubscriptionsByUserIdQuery query) {
        return subscriptionRepository.findByUserId(query.userId(), query.page(), query.size());
    }

    @Override
    public long countSubscriptionsByUser(UserId userId) {
        return subscriptionRepository.countByUserId(userId);
    }

    @Override
    public List<Subscription> handle(GetSubscriptionsByCommunityIdQuery query) {
        return subscriptionRepository.findByCommunityId(query.communityId());
    }
}
