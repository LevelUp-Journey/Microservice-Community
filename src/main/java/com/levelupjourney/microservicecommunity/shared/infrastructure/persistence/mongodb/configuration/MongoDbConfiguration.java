package com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB Configuration for the application.
 * Configures MongoDB client and enables repositories.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.levelupjourney.microservicecommunity.bounded.infrastructure.persistence.mongodb.repositories")
public class MongoDbConfiguration extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "microservice-community";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
