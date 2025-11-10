package com.levelup.journey.platform.user.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * User entity stored in MongoDB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true, name = "idx_users_user_id")
    private String userId;

    @Indexed(unique = true, name = "idx_users_profile_id")
    private String profileId;

    private Instant createdAt;
}
