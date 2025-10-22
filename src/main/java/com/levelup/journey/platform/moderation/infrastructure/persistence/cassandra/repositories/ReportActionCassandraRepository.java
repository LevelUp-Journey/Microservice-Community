package com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities.ReportActionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Cassandra Repository for ReportAction entities
 */
@Repository
public interface ReportActionCassandraRepository extends CassandraRepository<ReportActionEntity, UUID> {
    
    @Query("SELECT * FROM report_actions WHERE report_id = ?0")
    List<ReportActionEntity> findByReportId(UUID reportId);
    
    @Query("DELETE FROM report_actions WHERE report_id = ?0")
    void deleteByReportId(UUID reportId);
}