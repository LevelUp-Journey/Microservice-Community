package com.levelup.journey.platform.shared.infrastructure.persistence.cassandra.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
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

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Cassandra Configuration
 * Configures connection to Cassandra / Azure Cosmos DB Cassandra API
 * Supports SSL, authentication, and custom timeouts for cloud environments
 */
@Configuration
@EnableCassandraRepositories(basePackages = {
        "com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories",
        "com.levelup.journey.platform.social.infrastructure.persistence.cassandra.repositories",
        "com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.repositories"
})
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.cassandra.port}")
    private int port;

    @Value("${spring.cassandra.keyspace-name}")
    private String keyspaceName;

    @Value("${spring.cassandra.local-datacenter}")
    private String localDatacenter;

    @Value("${spring.cassandra.username}")
    private String username;

    @Value("${spring.cassandra.password}")
    private String password;

    @Value("${spring.cassandra.ssl:false}")
    private boolean sslEnabled;

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
            "com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities",
            "com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities",
            "com.levelup.journey.platform.moderation.infrastructure.persistence.cassandra.entities"
        };
    }

    @Bean
    @Override
    public CqlSessionFactoryBean cassandraSession() {
        CqlSessionFactoryBean session = super.cassandraSession();
        session.setUsername(username);
        session.setPassword(password);
        session.setSessionBuilderConfigurer(getSessionBuilderConfigurer());
        return session;
    }

    /**
     * Creates an SSL context that trusts all certificates.
     * This is needed for Azure Cosmos DB Cassandra API.
     */
    private SSLContext createSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new javax.net.ssl.TrustManager[] {
            new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        }, new java.security.SecureRandom());
        return sslContext;
    }

    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return sessionBuilder -> {
            try {
                // Configure driver with extended timeouts for cloud environments
                DriverConfigLoader configLoader = DriverConfigLoader.programmaticBuilder()
                        .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(15))
                        .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(15))
                        .withDuration(DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(15))
                        .withDuration(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, Duration.ofSeconds(15))
                        .build();

                // Build system session builder
                var systemSessionBuilder = CqlSession.builder()
                        .addContactPoint(new InetSocketAddress(contactPoints, port))
                        .withLocalDatacenter(localDatacenter)
                        .withAuthCredentials(username, password)
                        .withConfigLoader(configLoader);

                // Add SSL if enabled
                if (sslEnabled) {
                    System.out.println("SSL enabled for Cassandra connection (datacenter: " + localDatacenter + ")");
                    SSLContext sslContext = createSSLContext();
                    systemSessionBuilder.withSslContext(sslContext);
                }

                // Create keyspace if it doesn't exist
                try (CqlSession systemSession = systemSessionBuilder.build()) {
                    System.out.println("Connected to Cassandra at " + contactPoints + ":" + port);

                    String createKeyspace = String.format(
                        "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = " +
                        "{'class': 'SimpleStrategy', 'replication_factor': 1}",
                        keyspaceName
                    );
                    systemSession.execute(createKeyspace);
                    System.out.println("Keyspace '" + keyspaceName + "' created or already exists");
                } catch (Exception e) {
                    System.err.println("Failed to create keyspace: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create keyspace", e);
                }

                // Configure the main session builder with the same settings
                sessionBuilder
                        .withConfigLoader(configLoader)
                        .withAuthCredentials(username, password);

                if (sslEnabled) {
                    sessionBuilder.withSslContext(createSSLContext());
                }

            } catch (Exception e) {
                System.err.println("Error configuring Cassandra session: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to configure Cassandra session", e);
            }

            return sessionBuilder;
        };
    }

    @Bean
    public NamingStrategy cassandraNamingStrategy() {
        return new SnakeCaseWithPluralizedTableNamingStrategy();
    }
}
