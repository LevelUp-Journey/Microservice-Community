package com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Report Entity for Cassandra persistence
 * Maps to the reports table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reports")
public class ReportEntity {

    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID id;

    @CassandraType(type = CassandraType.Name.UUID)
    private UUID postId;

    @CassandraType(type = CassandraType.Name.UUID)
    private UUID reportedUserId;

    @CassandraType(type = CassandraType.Name.UUID)
    private UUID reporterUserId;

    private String category;
    private String severity;
    private String description;
    private String status;
    private boolean autoDetected;
    
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    @Builder.Default
    private List<String> suspiciousWords = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}