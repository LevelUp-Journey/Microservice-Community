package com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities.ReportEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Cassandra Repository for Report entities
 */
@Repository
public interface ReportCassandraRepository extends CassandraRepository<ReportEntity, UUID> {
    
    @Query("SELECT * FROM reports WHERE post_id = ?0")
    List<ReportEntity> findByPostId(UUID postId);
    
    @Query("SELECT * FROM reports WHERE category = ?0")
    List<ReportEntity> findByCategory(String category);
    
    @Query("SELECT * FROM reports WHERE reported_user_id = ?0")
    List<ReportEntity> findByReportedUserId(UUID reportedUserId);
    
    @Query("SELECT * FROM reports WHERE auto_detected = ?0")
    List<ReportEntity> findByAutoDetected(Boolean autoDetected);
}