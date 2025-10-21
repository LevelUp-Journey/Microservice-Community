package com.levelup.journey.platform.domain.model.repositories;

import com.levelup.journey.platform.domain.model.aggregates.Community;
import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;

import java.util.List;
import java.util.Optional;

/** Puerto de repositorio para Community. */
public interface CommunityRepository {
    Community save(Community community);
    Optional<Community> findById(CommunityId id);
    List<Community> findAll();
}

