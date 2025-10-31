package com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.repositories.ReportRepository;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities.ReportActionEntity;
import com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities.ReportEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ReportRepositoryAdapter implements ReportRepository {

    private final ReportCassandraRepository reportRepository;
    private final ReportActionCassandraRepository reportActionRepository;

    public ReportRepositoryAdapter(ReportCassandraRepository reportRepository,
                                   ReportActionCassandraRepository reportActionRepository) {
        this.reportRepository = reportRepository;
        this.reportActionRepository = reportActionRepository;
    }

    @Override
    public ContentReport save(ContentReport contentReport) {
        ReportEntity entity = fromAggregate(contentReport);
        ReportEntity savedEntity = reportRepository.save(entity);
        
        // Save report actions
        List<ReportActionEntity> actionEntities = contentReport.getActions().stream()
                .map(action -> ReportActionEntity.builder()
                        .reportId(savedEntity.getId())
                        .actionId(UUID.randomUUID())
                        .actionType(action.getActionType().name())
                        .reason(action.getReason())
                        .performedBy(action.getPerformedBy() != null ? action.getPerformedBy().value() : null)
                        .performedAt(action.getPerformedAt())
                        .build())
                .collect(Collectors.toList());
        
        reportActionRepository.saveAll(actionEntities);
        
        return toAggregate(savedEntity, actionEntities);
    }

    @Override
    public Optional<ContentReport> findById(ReportId reportId) {
        Optional<ReportEntity> reportEntity = reportRepository.findById(reportId.value());
        
        if (reportEntity.isEmpty()) {
            return Optional.empty();
        }
        
        List<ReportActionEntity> actions = reportActionRepository.findByReportId(reportId.value());
        return Optional.of(toAggregate(reportEntity.get(), actions));
    }

    @Override
    public List<ContentReport> findByPostId(PostId postId) {
        List<ReportEntity> entities = reportRepository.findByPostId(UUID.fromString(postId.value().toString()));
        return entities.stream()
                .map(entity -> {
                    List<ReportActionEntity> actions = reportActionRepository.findByReportId(entity.getId());
                    return toAggregate(entity, actions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentReport> findByCategory(ReportCategory category) {
        List<ReportEntity> entities = reportRepository.findByCategory(category.name());
        return entities.stream()
                .map(entity -> {
                    List<ReportActionEntity> actions = reportActionRepository.findByReportId(entity.getId());
                    return toAggregate(entity, actions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentReport> findAll() {
        List<ReportEntity> entities = reportRepository.findAll();
        return entities.stream()
                .map(entity -> {
                    List<ReportActionEntity> actions = reportActionRepository.findByReportId(entity.getId());
                    return toAggregate(entity, actions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ReportId reportId) {
        reportActionRepository.deleteByReportId(reportId.value());
        reportRepository.deleteById(reportId.value());
    }

    @Override
    public List<ContentReport> findByReportedUserId(UserId userId) {
        List<ReportEntity> entities = reportRepository.findByReportedUserId(userId.value());
        return entities.stream()
                .map(entity -> {
                    List<ReportActionEntity> actions = reportActionRepository.findByReportId(entity.getId());
                    return toAggregate(entity, actions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentReport> findAutoDetectedReports() {
        List<ReportEntity> entities = reportRepository.findByAutoDetected(true);
        return entities.stream()
                .map(entity -> {
                    List<ReportActionEntity> actions = reportActionRepository.findByReportId(entity.getId());
                    return toAggregate(entity, actions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(ReportId id) {
        return reportRepository.existsById(id.value());
    }

    private ReportEntity fromAggregate(ContentReport contentReport) {
        return ReportEntity.builder()
                .id(contentReport.getId().value())
                .postId(UUID.fromString(contentReport.getPostId().value().toString()))
                .reportedUserId(contentReport.getReportedUserId().value())
                .reporterUserId(contentReport.getReporterUserId() != null ? 
                    contentReport.getReporterUserId().value() : null)
                .category(contentReport.getCategory().name())
                .severity(contentReport.getSeverity().name())
                .description(contentReport.getDescription())
                .status(contentReport.getStatus().name())
                .autoDetected(contentReport.isAutoDetected())
                .suspiciousWords(contentReport.getSuspiciousWords())
                .createdAt(contentReport.getCreatedAt())
                .updatedAt(contentReport.getUpdatedAt())
                .build();
    }

    private ContentReport toAggregate(ReportEntity entity, List<ReportActionEntity> actionEntities) {
        // Convert action entities to domain entities
        var actions = actionEntities.stream()
                .map(actionEntity -> new com.levelup.journey.platform.moderation.domain.model.entities.ReportAction(
                        com.levelup.journey.platform.moderation.domain.model.valueobjects.ActionType.valueOf(actionEntity.getActionType()),
                        actionEntity.getReason(),
                        actionEntity.getPerformedBy() != null ? new UserId(actionEntity.getPerformedBy()) : null,
                        actionEntity.getPerformedAt()
                ))
                .collect(Collectors.toList());

        return ContentReport.restore(
                new ReportId(entity.getId()),
                new PostId(entity.getPostId()),
                entity.getReporterUserId() != null ? new UserId(entity.getReporterUserId()) : null,
                new UserId(entity.getReportedUserId()),
                ReportCategory.valueOf(entity.getCategory()),
                com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportSeverity.valueOf(entity.getSeverity()),
                entity.getDescription(),
                com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportStatus.valueOf(entity.getStatus()),
                entity.isAutoDetected(),
                entity.getSuspiciousWords(),
                actions,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}