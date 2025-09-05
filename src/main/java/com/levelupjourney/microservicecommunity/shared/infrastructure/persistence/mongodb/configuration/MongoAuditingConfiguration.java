package com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

/**
 * MongoDB Auditing Configuration.
 * Enables automatic auditing for MongoDB documents.
 */
@Configuration
@EnableMongoAuditing
public class MongoAuditingConfiguration {

    /**
     * Provides the current auditor for auditing purposes.
     * In a real application, this would return the current user.
     * 
     * @return AuditorAware bean for auditing
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system"); // Replace with actual user context
    }
}
