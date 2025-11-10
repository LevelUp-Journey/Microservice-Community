package com.levelup.journey.platform.user.infrastructure.messaging.kafka.configuration;

import com.levelup.journey.platform.user.infrastructure.messaging.kafka.events.UserRegistrationKafkaEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for user registration events
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Creates the consumer factory for UserRegistrationKafkaEvent
     */
    @Bean
    public ConsumerFactory<String, UserRegistrationKafkaEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Fix for Kafka advertised listeners issue - use IP directly
        config.put("client.dns.lookup", "use_all_dns_ips");
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);

        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserRegistrationKafkaEvent.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(UserRegistrationKafkaEvent.class, false)
        );
    }

    /**
     * Creates the Kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegistrationKafkaEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserRegistrationKafkaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
