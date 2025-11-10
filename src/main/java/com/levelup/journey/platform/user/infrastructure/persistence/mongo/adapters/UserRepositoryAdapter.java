package com.levelup.journey.platform.user.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.repositories.UserRepository;
import com.levelup.journey.platform.user.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.user.infrastructure.persistence.mongo.entities.UserEntity;
import com.levelup.journey.platform.user.infrastructure.persistence.mongo.repositories.UserMongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * User repository adapter backed by MongoDB
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository mongoRepository;

    public UserRepositoryAdapter(UserMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        mongoRepository.save(entity);
        return user;
    }

    @Override
    public Optional<User> findByUserId(UserId userId) {
        return mongoRepository.findByUserId(userId.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByProfileId(ProfileId profileId) {
        return mongoRepository.findByProfileId(profileId.value())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return mongoRepository.existsByUserId(userId.value());
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getUserId().value(), // Using userId as MongoDB _id
                user.getUserId().value(),
                user.getProfileId().value(),
                user.getCreatedAt()
        );
    }

    private User toDomain(UserEntity entity) {
        return User.restore(
                UserId.of(entity.getUserId()),
                ProfileId.of(entity.getProfileId()),
                entity.getCreatedAt()
        );
    }
}
