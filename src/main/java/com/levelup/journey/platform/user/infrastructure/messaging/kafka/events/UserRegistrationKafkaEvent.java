package com.levelup.journey.platform.user.infrastructure.messaging.kafka.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Kafka event for user registration
 * Maps the incoming Kafka event to a Java object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationKafkaEvent {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("profileId")
    private String profileId;

    @JsonProperty("occurredOn")
    private int[] occurredOn;

    /**
     * Converts the occurredOn array to an Instant
     * The array format is: [year, month, day, hour, minute, second, nanos]
     *
     * @return the Instant representation of the occurredOn timestamp
     */
    public Instant getOccurredOnAsInstant() {
        if (occurredOn == null || occurredOn.length < 7) {
            return Instant.now();
        }

        try {
            LocalDateTime dateTime = LocalDateTime.of(
                    occurredOn[0], // year
                    occurredOn[1], // month
                    occurredOn[2], // day
                    occurredOn[3], // hour
                    occurredOn[4], // minute
                    occurredOn[5], // second
                    occurredOn[6]  // nanos
            );
            return dateTime.toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            return Instant.now();
        }
    }
}
