package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.PublishPostCommand;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Post Command Service Implementation
 * Handles commands for Post aggregate
 */
@Service
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;

    public PostCommandServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Optional<Post> handle(PublishPostCommand command) {
        // Create new post from command
        Post post = Post.publish(
                command.id(),
                command.communityId(),
                command.authorId(),
                command.title(),
                command.content()
        );

        // Save post
        postRepository.save(post);

        return Optional.of(post);
    }

    @Override
    public Optional<Post> handle(AddCommentCommand command) {
        // Find existing post
        Optional<Post> postOptional = postRepository.findById(command.postId());

        if (postOptional.isEmpty()) {
            return Optional.empty();
        }

        Post post = postOptional.get();

        // Add comment to post
        post.addComment(
                command.commentId(),
                command.authorId(),
                command.content()
        );

        // Save updated post
        postRepository.save(post);

        return Optional.of(post);
    }
}
