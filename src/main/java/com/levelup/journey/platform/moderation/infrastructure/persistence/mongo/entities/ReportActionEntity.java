package com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Report action entity stored in its own MongoDB collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_actions")
public class ReportActionEntity {

    @Id
    private UUID id;

    @Indexed
    @Field(name = "report_id")
    private UUID reportId;

    private String actionType;
    private String reason;

    @Field(name = "performed_by")
    private UUID performedBy;

    private LocalDateTime performedAt;
}
