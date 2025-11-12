package com.levelup.journey.platform.post.infrastructure.acl;

import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.shared.domain.acl.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ACL Adapter for community services.
 * Translates between the shared ACL interface and the Post bounded context.
 */
@Service
public class CommunityServiceAclAdapter implements CommunityService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityServiceAclAdapter.class);

    private final CommunityQueryService communityQueryService;

    public CommunityServiceAclAdapter(CommunityQueryService communityQueryService) {
        this.communityQueryService = communityQueryService;
    }

    @Override
    public String getCommunityName(String communityId) {
        try {
            var query = new GetCommunityByIdQuery(
                com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId.of(communityId)
            );
            var community = communityQueryService.handle(query);

            return community.map(c -> c.name()).orElse(null);

        } catch (Exception e) {
            logger.error("Error getting community name for ID: {}", communityId, e);
            return null;
        }
    }

    @Override
    public String getCommunityImageUrl(String communityId) {
        try {
            var query = new GetCommunityByIdQuery(
                com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId.of(communityId)
            );
            var community = communityQueryService.handle(query);

            return community.map(c -> c.imageUrl() != null ? c.imageUrl().url() : null).orElse(null);

        } catch (Exception e) {
            logger.error("Error getting community image URL for ID: {}", communityId, e);
            return null;
        }
    }
}