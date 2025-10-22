package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.repositories.ReactionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.ReactionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories.ReactionCassandraRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
        return cassandraRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Reaction> findByPostId(PostId postId) {
        return cassandraRepository.findByPostId(postId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByUserId(UserId userId) {
        return cassandraRepository.findByUserId(userId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reaction> findByPostIdAndUserId(PostId postId, UserId userId) {
        return cassandraRepository.findByPostIdAndUserId(postId.value(), userId.value())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(ReactionId id) {
        cassandraRepository.deleteById(id.value());
    }

    /**
     * Convert domain Reaction to Cassandra entity
     */
    private ReactionEntity toEntity(Reaction reaction) {
        return new ReactionEntity(
                reaction.id().value(),
                reaction.postId().value(),
                reaction.userId().value(),
                reaction.reactionType().name(),
                reaction.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Reaction
     */
    private Reaction toDomain(ReactionEntity entity) {
        return Reaction.restore(
                ReactionId.of(entity.getId()),
                PostId.of(entity.getPostId()),
                UserId.of(entity.getUserId()),
                ReactionType.valueOf(entity.getReactionType()),
                entity.getCreatedAt()
        );
    }
}
