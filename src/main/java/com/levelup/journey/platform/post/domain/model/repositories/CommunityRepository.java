package com.levelup.journey.platform.post.domain.model.repositories;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/** Puerto de repositorio para Community. */
public interface CommunityRepository {
    Community save(Community community);
    Optional<Community> findById(CommunityId id);
    List<Community> findAll();
    void deleteById(CommunityId id);
    List<Community> findByOwnerId(UserId ownerId);
}

