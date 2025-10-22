package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.SubscriptionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories.SubscriptionCassandraRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Subscription Repository Adapter
 * Adapts domain Subscription repository to Cassandra persistence
 */
@Component
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionCassandraRepository cassandraRepository;

    public SubscriptionRepositoryAdapter(SubscriptionCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        cassandraRepository.save(entity);
        return subscription;
    }

    @Override
    public Optional<Subscription> findById(SubscriptionId id) {
        return cassandraRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Subscription> findByUserId(UserId userId) {
        return cassandraRepository.findByUserId(userId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subscription> findByCommunityId(CommunityId communityId) {
        return cassandraRepository.findByCommunityId(communityId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Subscription> findByUserIdAndCommunityId(UserId userId, CommunityId communityId) {
        return cassandraRepository.findByUserIdAndCommunityId(userId.value(), communityId.value())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(SubscriptionId id) {
        cassandraRepository.deleteById(id.value());
    }

    /**
     * Convert domain Subscription to Cassandra entity
     */
    private SubscriptionEntity toEntity(Subscription subscription) {
        return new SubscriptionEntity(
                subscription.id().value(),
                subscription.userId().value(),
                subscription.communityId().value(),
                subscription.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Subscription
     */
    private Subscription toDomain(SubscriptionEntity entity) {
        return Subscription.restore(
                SubscriptionId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                CommunityId.of(entity.getCommunityId()),
                entity.getCreatedAt()
        );
    }
}
