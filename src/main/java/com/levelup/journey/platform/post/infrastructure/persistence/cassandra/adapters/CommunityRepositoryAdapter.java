package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.CommunityEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories.CommunityCassandraRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Community Repository Adapter
 * Adapts domain Community repository to Cassandra persistence
 */
@Component
public class CommunityRepositoryAdapter implements CommunityRepository {

    private final CommunityCassandraRepository cassandraRepository;

    public CommunityRepositoryAdapter(CommunityCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public Community save(Community community) {
        CommunityEntity entity = toEntity(community);
        cassandraRepository.save(entity);
        return community;
    }

    @Override
    public Optional<Community> findById(CommunityId id) {
        return cassandraRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Community> findAll() {
        return cassandraRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert domain Community to Cassandra entity
     */
    private CommunityEntity toEntity(Community community) {
        return new CommunityEntity(
                community.id().value(),
                community.ownerId().value(),
                community.name(),
                community.description(),
                community.imageUrl() != null && !community.imageUrl().isEmpty() ? community.imageUrl().url() : null,
                community.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Community
     */
    private Community toDomain(CommunityEntity entity) {
        ImageUrl imageUrl = entity.getImageUrl() != null ? ImageUrl.of(entity.getImageUrl()) : ImageUrl.empty();
        return Community.restore(
                CommunityId.of(entity.getId()),
                UserId.of(entity.getOwnerId()),
                entity.getName(),
                entity.getDescription(),
                imageUrl,
                entity.getCreatedAt()
        );
    }
}
