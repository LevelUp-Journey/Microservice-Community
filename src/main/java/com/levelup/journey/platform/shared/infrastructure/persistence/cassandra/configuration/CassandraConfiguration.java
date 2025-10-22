package com.levelup.journey.platform.shared.infrastructure.persistence.cassandra.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import com.levelup.journey.platform.shared.infrastructure.persistence.cassandra.configuration.strategy.SnakeCaseWithPluralizedTableNamingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionBuilderConfigurer;
import org.springframework.data.cassandra.core.mapping.NamingStrategy;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.net.InetSocketAddress;

/**
 * Cassandra Local Configuration
 * Configures connection to local Cassandra instance at 127.0.0.1:9042
 */
@Configuration
@EnableCassandraRepositories(basePackages = "com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories")
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.cassandra.port}")
    private int port;

    @Value("${spring.cassandra.keyspace-name}")
    private String keyspaceName;

    @Value("${spring.cassandra.local-datacenter}")
    private String localDatacenter;

    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDatacenter;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{
            "com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities"
        };
    }

    @Bean
    @Override
    public CqlSessionFactoryBean cassandraSession() {
        CqlSessionFactoryBean session = super.cassandraSession();
        session.setSessionBuilderConfigurer(getSessionBuilderConfigurer());
        return session;
    }

    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return sessionBuilder -> {
            // Build a system session first to create the keyspace if it doesn't exist
            try (CqlSession systemSession = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(contactPoints, port))
                    .withLocalDatacenter(localDatacenter)
                    .build()) {

                // Create keyspace if it doesn't exist
                String createKeyspace = String.format(
                    "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = " +
                    "{'class': 'SimpleStrategy', 'replication_factor': 1}",
                    keyspaceName
                );
                systemSession.execute(createKeyspace);
                
                System.out.println("Keyspace '" + keyspaceName + "' created or already exists");
            } catch (Exception e) {
                System.err.println("Error creating keyspace: " + e.getMessage());
                throw new RuntimeException("Failed to create keyspace", e);
            }

            return sessionBuilder;
        };
    }

    @Bean
    public NamingStrategy cassandraNamingStrategy() {
        return new SnakeCaseWithPluralizedTableNamingStrategy();
    }
}
