package com.levelup.journey.platform.moderation.domain.model.valueobjects;

import java.util.UUID;

public record ReportId(UUID value) {
    public ReportId {
        if (value == null) {
            throw new IllegalArgumentException("ReportId cannot be null");
        }
    }
    
    public static ReportId generate() {
        return new ReportId(UUID.randomUUID());
    }
    
    public static ReportId of(String value) {
        return new ReportId(UUID.fromString(value));
    }
}