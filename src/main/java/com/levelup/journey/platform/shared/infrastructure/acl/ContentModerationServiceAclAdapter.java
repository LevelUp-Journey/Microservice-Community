package com.levelup.journey.platform.shared.infrastructure.acl;

import com.levelup.journey.platform.moderation.domain.model.commands.AnalyzeContentCommand;
import com.levelup.journey.platform.moderation.domain.services.ReportCommandService;
import com.levelup.journey.platform.shared.domain.acl.ContentModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ACL Adapter for content moderation services.
 * Translates between the shared ACL interface and the Moderation bounded context.
 */
@Service
public class ContentModerationServiceAclAdapter implements ContentModerationService {

    private static final Logger logger = LoggerFactory.getLogger(ContentModerationServiceAclAdapter.class);

    private final ReportCommandService reportCommandService;

    public ContentModerationServiceAclAdapter(ReportCommandService reportCommandService) {
        this.reportCommandService = reportCommandService;
    }

    @Override
    public String analyzeContent(String contentId, String authorId, String content) {
        try {
            // Translate from shared ACL parameters to Moderation context command
            AnalyzeContentCommand command = new AnalyzeContentCommand(
                com.levelup.journey.platform.moderation.domain.model.valueobjects.PostId.of(
                    UUID.fromString(contentId)
                ),
                com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId.of(
                    UUID.fromString(authorId)
                ),
                content
            );

            // Execute the moderation analysis
            var reportId = reportCommandService.handle(command);

            // Translate back to shared ACL return type
            return reportId != null ? reportId.value().toString() : null;

        } catch (Exception e) {
            logger.error("Error in content moderation ACL adapter for content {}: {}",
                        contentId, e.getMessage());
            // Return null to indicate no violation detected (fail-safe approach)
            return null;
        }
    }
}