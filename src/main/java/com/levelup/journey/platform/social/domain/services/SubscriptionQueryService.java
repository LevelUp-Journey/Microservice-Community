package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByCommunityIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetSubscriptionsByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Subscription Query Service
 * Defines read operations for subscriptions
 */
public interface SubscriptionQueryService {

    /**
     * Handles the query to get a subscription by its ID
     * @param query the get subscription by id query
     * @return the subscription if found
     */
    Optional<Subscription> handle(GetSubscriptionByIdQuery query);

    /**
     * Handles the query to get subscriptions by user ID
     * @param query the get subscriptions by user id query
     * @return list of subscriptions for the user
     */
    List<Subscription> handle(GetSubscriptionsByUserIdQuery query);

    /**
     * Handles the query to get subscriptions by community ID
     * @param query the get subscriptions by community id query
     * @return list of subscriptions for the community
     */
    List<Subscription> handle(GetSubscriptionsByCommunityIdQuery query);
}
