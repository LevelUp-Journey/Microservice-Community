package com.levelup.journey.platform.social.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.repositories.FollowRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities.FollowEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories.FollowMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Follow repository adapter backed by MongoDB.
 */
@Component
public class FollowRepositoryAdapter implements FollowRepository {

    private final FollowMongoRepository mongoRepository;

    public FollowRepositoryAdapter(FollowMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Follow save(Follow follow) {
        FollowEntity entity = toEntity(follow);
        mongoRepository.save(entity);
        return follow;
    }

    @Override
    public Optional<Follow> findById(FollowId id) {
        return mongoRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Follow> findByFollowerId(UserId followerId) {
        return mongoRepository.findByFollowerId(followerId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Follow> findByFollowingId(UserId followingId) {
        return mongoRepository.findByFollowingId(followingId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFollowingId(UserId followerId, UserId followingId) {
        return mongoRepository.findByFollowerIdAndFollowingId(followerId.value(), followingId.value())
                .map(this::toDomain);
    }

    @Override
    public long countByFollowingId(UserId followingId) {
        return mongoRepository.countByFollowingId(followingId.value());
    }

    @Override
    public void deleteById(FollowId id) {
        mongoRepository.deleteById(id.value());
    }

    private FollowEntity toEntity(Follow follow) {
        return new FollowEntity(
                follow.id().value(),
                follow.followerId().value(),
                follow.followingId().value(),
                follow.createdAt()
        );
    }

    private Follow toDomain(FollowEntity entity) {
        return Follow.restore(
                FollowId.of(entity.getId()),
                UserId.of(entity.getFollowerId()),
                UserId.of(entity.getFollowingId()),
                entity.getCreatedAt()
        );
    }
}
