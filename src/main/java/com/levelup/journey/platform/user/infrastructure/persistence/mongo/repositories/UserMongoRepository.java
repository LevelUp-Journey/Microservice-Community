package com.levelup.journey.platform.user.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.user.infrastructure.persistence.mongo.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for User entities
 */
@Repository
public interface UserMongoRepository extends MongoRepository<UserEntity, String> {

    /**
     * Finds a user by user ID
     * @param userId the user ID
     * @return an Optional containing the user entity if found
     */
    Optional<UserEntity> findByUserId(String userId);

    /**
     * Finds a user by profile ID
     * @param profileId the profile ID
     * @return an Optional containing the user entity if found
     */
    Optional<UserEntity> findByProfileId(String profileId);

    /**
     * Checks if a user exists by user ID
     * @param userId the user ID
     * @return true if the user exists, false otherwise
     */
    boolean existsByUserId(String userId);
}
