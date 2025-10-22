package com.levelup.journey.platform.post.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.queries.GetAllCommunitiesQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Community Query Service Implementation
 * Handles queries for Community aggregate
 */
@Service
public class CommunityQueryServiceImpl implements CommunityQueryService {

    private final CommunityRepository communityRepository;

    public CommunityQueryServiceImpl(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public Optional<Community> handle(GetCommunityByIdQuery query) {
        return communityRepository.findById(query.id());
    }

    @Override
    public List<Community> handle(GetAllCommunitiesQuery query) {
        return communityRepository.findAll();
    }
}
