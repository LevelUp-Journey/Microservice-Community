package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.ReactionSummaryResource;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByPostIdQuery;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.application.internal.outboundservices.acl.ExternalUserService;
import com.levelup.journey.platform.social.domain.services.ReactionQueryService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Assembler to transform Post entity to PostResource
 */
@Component
public class PostResourceFromEntityAssembler {

    private final ReactionQueryService reactionQueryService;
    private final ExternalUserService externalUserService;

    public PostResourceFromEntityAssembler(ReactionQueryService reactionQueryService,
                                         ExternalUserService externalUserService) {
        this.reactionQueryService = reactionQueryService;
        this.externalUserService = externalUserService;
    }

    /**
     * Transform Post entity to PostResource
     * @param entity the post entity
     * @return the post resource with reaction summary
     */
    public PostResource toResourceFromEntity(Post entity) {
        return toResourceFromEntity(entity, null);
    }

    /**
     * Transform Post entity to PostResource with user-specific reaction data
     * @param entity the post entity
     * @param currentUserId the current authenticated user's ID (optional)
     * @return the post resource with reaction summary including user's reaction
     */
    public PostResource toResourceFromEntity(Post entity, String currentUserId) {
        // Fetch reactions for this post
        var reactionsQuery = new GetReactionsByPostIdQuery(PostId.of(entity.id().value()));
        var reactions = reactionQueryService.handle(reactionsQuery);

        // Calculate aggregated counts by reaction type
        Map<String, Integer> reactionCounts = reactions.stream()
                .collect(Collectors.groupingBy(
                        reaction -> reaction.reactionType().name(),
                        Collectors.summingInt(reaction -> 1)
                ));

        // Determine current user's reaction if user is authenticated
        String userReaction = null;
        if (currentUserId != null && !currentUserId.isBlank()) {
            userReaction = reactions.stream()
                    .filter(reaction -> reaction.userId().value().equals(currentUserId))
                    .findFirst()
                    .map(reaction -> reaction.reactionType().name())
                    .orElse(null);
        }

        ReactionSummaryResource reactionSummary = new ReactionSummaryResource(
                reactionCounts.isEmpty() ? new HashMap<>() : reactionCounts,
                userReaction
        );

        // Fetch author data from user context
        String authorName = externalUserService.fetchUsernameByUserId(entity.authorId().value());
        String authorProfileUrl = externalUserService.fetchProfileUrlByUserId(entity.authorId().value());

        return new PostResource(
                entity.id().value(),
                entity.communityId().value(),
                entity.authorId().value(),
                entity.authorProfileId().value(),
                authorName,
                authorProfileUrl,
                entity.content(),
                entity.imageUrl().isEmpty() ? null : entity.imageUrl().url(),
                entity.createdAt(),
                reactionSummary
        );
    }
}
