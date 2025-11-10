package com.levelup.journey.platform.shared.infrastructure.persistence.mongo.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.List;

/**
 * Central Mongo configuration for the LevelUp platform.
 * Enables repositories across all bounded contexts that now target MongoDB.
 */
@Configuration
@EnableMongoRepositories(basePackages = {
        "com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories",
        "com.levelup.journey.platform.social.infrastructure.persistence.mongo.repositories",
        "com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.repositories",
        "com.levelup.journey.platform.user.infrastructure.persistence.mongo.repositories"
})
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(mongoUri));
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Override
    public MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), getDatabaseName());
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return List.of(
                "com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities",
                "com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities",
                "com.levelup.journey.platform.moderation.infrastructure.persistence.mongo.entities",
                "com.levelup.journey.platform.user.infrastructure.persistence.mongo.entities"
        );
    }
}
