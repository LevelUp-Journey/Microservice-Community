package com.levelup.journey.platform.social.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.SubscriptionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories.SubscriptionMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mongo-backed repository adapter for subscriptions.
 */
@Component
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionMongoRepository mongoRepository;

    public SubscriptionRepositoryAdapter(SubscriptionMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        mongoRepository.save(entity);
        return subscription;
    }

    @Override
    public Optional<Subscription> findById(SubscriptionId id) {
        return mongoRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Subscription> findByUserId(UserId userId) {
        return mongoRepository.findByUserId(userId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subscription> findByCommunityId(CommunityId communityId) {
        return mongoRepository.findByCommunityId(communityId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Subscription> findByUserIdAndCommunityId(UserId userId, CommunityId communityId) {
        return mongoRepository.findByUserIdAndCommunityId(userId.value(), communityId.value())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(SubscriptionId id) {
        mongoRepository.deleteById(id.value());
    }

    private SubscriptionEntity toEntity(Subscription subscription) {
        return new SubscriptionEntity(
                subscription.id().value(),
                subscription.userId().value(),
                subscription.communityId().value(),
                subscription.createdAt()
        );
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        return Subscription.restore(
            SubscriptionId.of(entity.getId()),
            UserId.of(entity.getUserId()),
            CommunityId.of(entity.getCommunityId()),
            entity.getCreatedAt()
        );
    }
}
