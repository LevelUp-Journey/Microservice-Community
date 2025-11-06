package com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ReportAction Entity for Cassandra
 * Separate table for actions with composite primary key (reportId, actionId)
 * This allows efficient querying of all actions for a report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("report_actions")
public class ReportActionEntity {

    @PrimaryKeyColumn(name = "report_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID reportId;

    @PrimaryKeyColumn(name = "action_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID actionId;

    private String actionType;
    private String reason;
    
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID performedBy;
    
    private LocalDateTime performedAt;
}
