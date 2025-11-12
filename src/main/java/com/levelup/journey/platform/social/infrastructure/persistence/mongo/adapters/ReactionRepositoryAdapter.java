package com.levelup.journey.platform.social.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.repositories.ReactionRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.ReactionEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories.ReactionMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mongo-backed repository adapter for reactions.
 */
@Component
public class ReactionRepositoryAdapter implements ReactionRepository {

    private final ReactionMongoRepository mongoRepository;

    public ReactionRepositoryAdapter(ReactionMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Reaction save(Reaction reaction) {
        ReactionEntity entity = toEntity(reaction);
        mongoRepository.save(entity);
        return reaction;
    }

    @Override
    public Optional<Reaction> findById(ReactionId id) {
        return mongoRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Reaction> findByPostId(PostId postId) {
        return mongoRepository.findByPostId(postId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByUserId(UserId userId) {
        return mongoRepository.findByUserId(userId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Reaction> findByPostIdAndUserId(PostId postId, UserId userId) {
        return mongoRepository.findByPostIdAndUserId(postId.value(), userId.value())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(ReactionId id) {
        mongoRepository.deleteById(id.value());
    }

    @Override
    public void deleteByPostId(PostId postId) {
        mongoRepository.deleteByPostId(postId.value());
    }

    private ReactionEntity toEntity(Reaction reaction) {
        return new ReactionEntity(
                reaction.id().value(),
                reaction.postId().value(),
                reaction.userId().value(),
                reaction.reactionType().name(),
                reaction.createdAt()
        );
    }

    private Reaction toDomain(ReactionEntity entity) {
        return Reaction.restore(
                PostId.of(entity.getPostId()),
                UserId.of(entity.getUserId()),
                ReactionType.valueOf(entity.getReactionType()),
                entity.getCreatedAt()
        );
    }
}
