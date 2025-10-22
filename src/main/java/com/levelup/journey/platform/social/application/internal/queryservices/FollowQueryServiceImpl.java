package com.levelup.journey.platform.social.application.internal.queryservices;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowersByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowingByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.repositories.FollowRepository;
import com.levelup.journey.platform.social.domain.services.FollowQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Follow Query Service Implementation
 * Handles queries for Follow aggregate
 */
@Service
public class FollowQueryServiceImpl implements FollowQueryService {

    private final FollowRepository followRepository;

    public FollowQueryServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public Optional<Follow> handle(GetFollowByIdQuery query) {
        return followRepository.findById(query.id());
    }

    @Override
    public List<Follow> handle(GetFollowersByUserIdQuery query) {
        return followRepository.findByFollowingId(query.followingId());
    }

    @Override
    public List<Follow> handle(GetFollowingByUserIdQuery query) {
        return followRepository.findByFollowerId(query.followerId());
    }
}
