package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.repositories.CommunityRepository;
import com.levelup.journey.platform.post.domain.services.CommunityCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Community Command Service Implementation
 * Handles commands for Community aggregate
 */
@Service
public class CommunityCommandServiceImpl implements CommunityCommandService {

    private final CommunityRepository communityRepository;

    public CommunityCommandServiceImpl(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public Optional<Community> handle(CreateCommunityCommand command) {
        // Create new community from command
        Community community = Community.create(
                command.id(),
                command.ownerId(),
                command.name(),
                command.description()
        );

        // Save community
        communityRepository.save(community);

        return Optional.of(community);
    }
}
