package com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.entities.ReportActionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Mongo repository for report action entities.
 */
@Repository
public interface ReportActionMongoRepository extends MongoRepository<ReportActionEntity, UUID> {

    List<ReportActionEntity> findByReportId(UUID reportId);

    void deleteByReportId(UUID reportId);
}
