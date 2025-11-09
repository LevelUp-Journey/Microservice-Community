package com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Report Entity for MongoDB persistence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class ReportEntity {

    @Id
    private UUID id;

    @Field(name = "post_id")
    private UUID postId;

    @Field(name = "reported_user_id")
    private UUID reportedUserId;

    @Field(name = "reporter_user_id")
    private UUID reporterUserId;

    private String category;
    private String severity;
    private String description;
    private String status;
    private boolean autoDetected;

    @Builder.Default
    private List<String> suspiciousWords = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
