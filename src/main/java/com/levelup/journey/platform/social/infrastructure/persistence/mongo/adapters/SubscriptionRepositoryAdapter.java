package com.levelup.journey.platform.social.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.domain.model.repositories.SubscriptionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.SubscriptionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.SubscriptionPrimaryKey;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories.SubscriptionCassandraRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        // Since we're using composite keys, we need to search through all subscriptions
        // This is inefficient but works for the current design
        return cassandraRepository.findAll().stream()
                .filter(entity -> {
                    String syntheticId = entity.getId().getUserId().toString() + "-" + entity.getId().getCommunityId().toString();
                    return syntheticId.equals(id.value());
                })
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<Subscription> findByUserId(UserId userId) {
        return cassandraRepository.findById_UserId(UUID.fromString(userId.value())).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subscription> findByCommunityId(CommunityId communityId) {
        return cassandraRepository.findById_CommunityId(UUID.fromString(communityId.value())).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Subscription> findByUserIdAndCommunityId(UserId userId, CommunityId communityId) {
        return cassandraRepository.findByUserIdAndCommunityId(UUID.fromString(userId.value()), UUID.fromString(communityId.value()))
                .map(this::toDomain);
    }

    @Override
    public void deleteById(SubscriptionId id) {
        // Since we're using composite keys, we need to find and delete the entity
        cassandraRepository.findAll().stream()
                .filter(entity -> {
                    String syntheticId = entity.getId().getUserId().toString() + "-" + entity.getId().getCommunityId().toString();
                    return syntheticId.equals(id.value());
                })
                .findFirst()
                .ifPresent(cassandraRepository::delete);
    }

    /**
     * Convert domain Subscription to Cassandra entity
     */
    private SubscriptionEntity toEntity(Subscription subscription) {
        SubscriptionPrimaryKey primaryKey = new SubscriptionPrimaryKey(
                UUID.fromString(subscription.userId().value()),
                UUID.fromString(subscription.communityId().value())
        );
        return new SubscriptionEntity(
                primaryKey,
                subscription.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Subscription
     */
    private Subscription toDomain(SubscriptionEntity entity) {
        // Create a synthetic SubscriptionId based on the composite key
        String syntheticId = entity.getId().getUserId().toString() + "-" + entity.getId().getCommunityId().toString();
        return Subscription.restore(
                SubscriptionId.of(syntheticId),
                UserId.of(entity.getId().getUserId().toString()),
                CommunityId.of(entity.getId().getCommunityId().toString()),
                entity.getCreatedAt()
        );
    }
}
