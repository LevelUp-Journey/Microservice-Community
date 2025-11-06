package com.levelup.journey.platform.shared.infrastructure.persistence.cassandra.configuration.strategy;

import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentProperty;
import org.springframework.data.cassandra.core.mapping.NamingStrategy;

import static io.github.encryptorcode.pluralize.Pluralize.pluralize;

/**
 * SnakeCase Naming Strategy for Cassandra
 * @summary
 * This class is used to convert the table names and column names to snake case.
 * It also pluralizes the table names.
 * It implements the NamingStrategy interface from Spring Data Cassandra.
 * @since 1.0
 * @see NamingStrategy
 */
public class SnakeCaseWithPluralizedTableNamingStrategy implements NamingStrategy {

    /**
     * Converts the Table Name to Snake Case and Pluralizes it
     * @param entity persistent entity
     * @return Snake Case and Pluralized Table Name
     */
    @Override
    public String getTableName(CassandraPersistentEntity<?> entity) {
        String simpleName = entity.getType().getSimpleName();
        String snakeCase = toSnakeCase(simpleName);
        return pluralize(snakeCase);
    }

    /**
     * Converts the Column Name to Snake Case
     * @param property persistent property
     * @return Snake Case Column Name
     */
    @Override
    public String getColumnName(CassandraPersistentProperty property) {
        return toSnakeCase(property.getName());
    }

    /**
     * Converts a name to Snake Case
     * @param name object name
     * @return Snake Case name
     */
    private String toSnakeCase(String name) {
        if (name == null) return null;

        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        return name.replaceAll(regex, replacement).toLowerCase();
    }
}
