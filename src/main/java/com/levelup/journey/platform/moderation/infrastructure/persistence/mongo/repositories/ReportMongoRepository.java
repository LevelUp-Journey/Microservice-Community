package com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.entities.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Mongo repository for report entities.
 */
@Repository
public interface ReportMongoRepository extends MongoRepository<ReportEntity, UUID> {

    List<ReportEntity> findByPostId(UUID postId);

    List<ReportEntity> findByCategory(String category);

    List<ReportEntity> findByReportedUserId(UUID reportedUserId);

    List<ReportEntity> findByAutoDetected(Boolean autoDetected);
}
