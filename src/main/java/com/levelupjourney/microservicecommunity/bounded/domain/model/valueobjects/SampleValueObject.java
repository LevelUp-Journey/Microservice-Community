package com.levelupjourney.microservicecommunity.bounded.domain.model.valueobjects;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * Sample Value Object demonstrating MongoDB DDD structure.
 * Value objects are immutable and defined by their attributes.
 * 
 * This demonstrates:
 * - Immutable design
 * - Value equality
 * - Business validation
 * - Embedded in aggregates
 */
@Getter
public class SampleValueObject {

    @Field("code")
    private final String code;

    @Field("display_name")
    private final String displayName;

    @Field("category")
    private final String category;

    @Field("priority")
    private final Integer priority;

    // Private default constructor for frameworks
    protected SampleValueObject() {
        this.code = null;
        this.displayName = null;
        this.category = null;
        this.priority = null;
    }

    /**
     * Constructor for creating a value object.
     * Performs validation to ensure object integrity.
     * 
     * @param code unique code for the value object
     * @param displayName human-readable display name
     * @param category category classification
     * @param priority numeric priority (1-10)
     */
    public SampleValueObject(String code, String displayName, String category, Integer priority) {
        // Validation
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }
        if (priority != null && (priority < 1 || priority > 10)) {
            throw new IllegalArgumentException("Priority must be between 1 and 10");
        }

        this.code = code.trim().toUpperCase();
        this.displayName = displayName.trim();
        this.category = category != null ? category.trim() : null;
        this.priority = priority;
    }

    /**
     * Business method to check if this value object has high priority.
     * 
     * @return true if priority is 8 or higher
     */
    public boolean isHighPriority() {
        return priority != null && priority >= 8;
    }

    /**
     * Business method to check if this value object belongs to a specific category.
     * 
     * @param categoryToCheck the category to check against
     * @return true if the value object belongs to the specified category
     */
    public boolean belongsToCategory(String categoryToCheck) {
        return category != null && category.equalsIgnoreCase(categoryToCheck);
    }

    /**
     * Business method to format the value object for display.
     * 
     * @return formatted string representation
     */
    public String formatForDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(displayName);
        if (category != null) {
            sb.append(" (").append(category).append(")");
        }
        if (priority != null) {
            sb.append(" - Priority: ").append(priority);
        }
        return sb.toString();
    }

    /**
     * Value objects are equal if all their attributes are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleValueObject that = (SampleValueObject) o;
        return Objects.equals(code, that.code) &&
               Objects.equals(displayName, that.displayName) &&
               Objects.equals(category, that.category) &&
               Objects.equals(priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, displayName, category, priority);
    }

    @Override
    public String toString() {
        return "SampleValueObject{" +
               "code='" + code + '\'' +
               ", displayName='" + displayName + '\'' +
               ", category='" + category + '\'' +
               ", priority=" + priority +
               '}';
    }
}
