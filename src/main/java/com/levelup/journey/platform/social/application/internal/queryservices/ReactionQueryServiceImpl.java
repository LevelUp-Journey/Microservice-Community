package com.levelup.journey.platform.social.application.internal.queryservices;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionByUserAndPostQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByPostIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.repositories.ReactionRepository;
import com.levelup.journey.platform.social.domain.services.ReactionQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Reaction Query Service Implementation
 * Handles queries for Reaction aggregate
 */
@Service
public class ReactionQueryServiceImpl implements ReactionQueryService {

    private final ReactionRepository reactionRepository;

    public ReactionQueryServiceImpl(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public Optional<Reaction> handle(GetReactionByIdQuery query) {
        return reactionRepository.findById(query.id());
    }

    @Override
    public List<Reaction> handle(GetReactionsByPostIdQuery query) {
        return reactionRepository.findByPostId(query.postId());
    }

    @Override
    public List<Reaction> handle(GetReactionsByUserIdQuery query) {
        return reactionRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<Reaction> handle(GetReactionByUserAndPostQuery query) {
        return reactionRepository.findByPostIdAndUserId(query.postId(), query.userId());
    }
}
