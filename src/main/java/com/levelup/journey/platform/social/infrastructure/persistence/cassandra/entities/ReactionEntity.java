package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

/**
 * Reaction Entity for Cassandra persistence
 * Maps to the reactions table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("reactions")
public class ReactionEntity {

    @PrimaryKey
    private ReactionPrimaryKey id;

    private String reactionType;
    private Instant createdAt;
}
