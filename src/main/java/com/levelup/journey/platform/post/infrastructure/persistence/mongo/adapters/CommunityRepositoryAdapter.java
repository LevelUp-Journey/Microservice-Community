package com.levelup.journey.platform.post.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities.CommunityEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories.CommunityMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mongo-backed repository adapter for communities.
 */
@Component
public class CommunityRepositoryAdapter implements CommunityRepository {

    private final CommunityMongoRepository mongoRepository;

    public CommunityRepositoryAdapter(CommunityMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Community save(Community community) {
        CommunityEntity entity = toEntity(community);
        mongoRepository.save(entity);
        return community;
    }

    @Override
    public Optional<Community> findById(CommunityId id) {
        return mongoRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Community> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(CommunityId id) {
        mongoRepository.deleteById(id.value());
    }

    private CommunityEntity toEntity(Community community) {
        return new CommunityEntity(
                community.id().value(),
                community.ownerId().value(),
                community.ownerProfileId().value(),
                community.name(),
                community.description(),
                community.imageUrl() != null && !community.imageUrl().isEmpty() ? community.imageUrl().url() : null,
                community.createdAt()
        );
    }

    private Community toDomain(CommunityEntity entity) {
        ImageUrl imageUrl = entity.getImageUrl() != null ? ImageUrl.of(entity.getImageUrl()) : ImageUrl.empty();
        return Community.restore(
                CommunityId.of(entity.getId()),
                UserId.of(entity.getOwnerId()),
                ProfileId.of(entity.getOwnerProfileId()),
                entity.getName(),
                entity.getDescription(),
                imageUrl,
                entity.getCreatedAt()
        );
    }
}
