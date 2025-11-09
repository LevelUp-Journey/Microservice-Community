package com.levelup.journey.platform.social.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.repositories.ReactionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.ReactionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.ReactionPrimaryKey;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories.ReactionCassandraRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Reaction Repository Adapter
 * Adapts domain Reaction repository to Cassandra persistence
 */
@Component
public class ReactionRepositoryAdapter implements ReactionRepository {

    private final ReactionCassandraRepository cassandraRepository;

    public ReactionRepositoryAdapter(ReactionCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public Reaction save(Reaction reaction) {
        ReactionEntity entity = toEntity(reaction);
        cassandraRepository.save(entity);
        return reaction;
    }

    @Override
    public Optional<Reaction> findById(ReactionId id) {
        // Since we're using composite keys, we need to search through all reactions
        // This is inefficient but works for the current design
        return cassandraRepository.findAll().stream()
                .filter(entity -> {
                    String syntheticId = entity.getId().getPostId().toString() + "-" + entity.getId().getUserId().toString();
                    return syntheticId.equals(id.value());
                })
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<Reaction> findByPostId(PostId postId) {
        return cassandraRepository.findById_PostId(UUID.fromString(postId.value())).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByUserId(UserId userId) {
        return cassandraRepository.findById_UserId(UUID.fromString(userId.value())).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reaction> findByPostIdAndUserId(PostId postId, UserId userId) {
        return cassandraRepository.findByPostIdAndUserId(UUID.fromString(postId.value()), UUID.fromString(userId.value()))
                .map(this::toDomain);
    }

    @Override
    public void deleteById(ReactionId id) {
        // Since we're using composite keys, we need to find and delete the entity
        cassandraRepository.findAll().stream()
                .filter(entity -> {
                    String syntheticId = entity.getId().getPostId().toString() + "-" + entity.getId().getUserId().toString();
                    return syntheticId.equals(id.value());
                })
                .findFirst()
                .ifPresent(cassandraRepository::delete);
    }

    /**
     * Convert domain Reaction to Cassandra entity
     */
    private ReactionEntity toEntity(Reaction reaction) {
        ReactionPrimaryKey primaryKey = new ReactionPrimaryKey(
                UUID.fromString(reaction.postId().value()),
                UUID.fromString(reaction.userId().value())
        );
        return new ReactionEntity(
                primaryKey,
                reaction.reactionType().name(),
                reaction.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Reaction
     */
    private Reaction toDomain(ReactionEntity entity) {
        return Reaction.restore(
                PostId.of(entity.getId().getPostId().toString()),
                UserId.of(entity.getId().getUserId().toString()),
                ReactionType.valueOf(entity.getReactionType()),
                entity.getCreatedAt()
        );
    }
}
