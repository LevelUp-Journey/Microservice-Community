package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommentResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommunityResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Assembler to transform Post entity to PostResource
 */
@Component
public class PostResourceFromEntityAssembler {

    private final CommunityQueryService communityQueryService;
    private final CommunityResourceFromEntityAssembler communityResourceAssembler;

    public PostResourceFromEntityAssembler(CommunityQueryService communityQueryService,
                                          CommunityResourceFromEntityAssembler communityResourceAssembler) {
        this.communityQueryService = communityQueryService;
        this.communityResourceAssembler = communityResourceAssembler;
    }

    public PostResource toResourceFromEntity(Post entity) {
        var comments = entity.comments().stream()
                .map(comment -> new CommentResource(
                        comment.id().value(),
                        comment.authorId().value(),
                        comment.authorProfileId().value(),
                        comment.content(),
                        comment.imageUrl().isEmpty() ? null : comment.imageUrl().url(),
                        comment.createdAt()
                ))
                .collect(Collectors.toList());

        // Fetch community data
        var communityQuery = new GetCommunityByIdQuery(entity.communityId());
        var communityOptional = communityQueryService.handle(communityQuery);

        CommunityResource communityResource = null;
        if (communityOptional.isPresent()) {
            communityResource = communityResourceAssembler.toResourceFromEntity(communityOptional.get());
        }

        return new PostResource(
                entity.id().value(),
                communityResource,
                entity.authorId().value(),
                entity.authorProfileId().value(),
                entity.content(),
                entity.imageUrl().isEmpty() ? null : entity.imageUrl().url(),
                entity.createdAt(),
                comments
        );
    }
}
