package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.repositories.FollowRepository;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities.FollowEntity;
import com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories.FollowCassandraRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Follow Repository Adapter
 * Adapts domain Follow repository to Cassandra persistence
 */
@Component
public class FollowRepositoryAdapter implements FollowRepository {

    private final FollowCassandraRepository cassandraRepository;

    public FollowRepositoryAdapter(FollowCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public Follow save(Follow follow) {
        FollowEntity entity = toEntity(follow);
        cassandraRepository.save(entity);
        return follow;
    }

    @Override
    public Optional<Follow> findById(FollowId id) {
        return cassandraRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Follow> findByFollowerId(UserId followerId) {
        return cassandraRepository.findByFollowerId(followerId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Follow> findByFollowingId(UserId followingId) {
        return cassandraRepository.findByFollowingId(followingId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFollowingId(UserId followerId, UserId followingId) {
        return cassandraRepository.findByFollowerIdAndFollowingId(followerId.value(), followingId.value())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(FollowId id) {
        cassandraRepository.deleteById(id.value());
    }

    /**
     * Convert domain Follow to Cassandra entity
     */
    private FollowEntity toEntity(Follow follow) {
        return new FollowEntity(
                follow.id().value(),
                follow.followerId().value(),
                follow.followingId().value(),
                follow.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Follow
     */
    private Follow toDomain(FollowEntity entity) {
        return Follow.restore(
                FollowId.of(entity.getId()),
                UserId.of(entity.getFollowerId()),
                UserId.of(entity.getFollowingId()),
                entity.getCreatedAt()
        );
    }
}
