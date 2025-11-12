package com.levelup.journey.platform.social.domain.model.repositories;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Reaction aggregate
 */
public interface ReactionRepository {
    Reaction save(Reaction reaction);
    Optional<Reaction> findById(ReactionId id);
    List<Reaction> findByPostId(PostId postId);
    List<Reaction> findByUserId(UserId userId);
    Optional<Reaction> findByPostIdAndUserId(PostId postId, UserId userId);
    void deleteById(ReactionId id);
    void deleteByPostId(PostId postId);
}
