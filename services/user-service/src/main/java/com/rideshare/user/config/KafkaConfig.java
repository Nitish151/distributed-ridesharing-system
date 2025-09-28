package com.rideshare.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rideshare.events.config.TopicNames;
import com.rideshare.events.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for User Service
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Producer reliability settings
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        // Performance settings
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public EventPublisher eventPublisher(KafkaTemplate<String, Object> kafkaTemplate, 
                                         ObjectMapper objectMapper) {
        return new EventPublisher(kafkaTemplate, objectMapper);
    }

    /**
     * Create user-events topic
     */
    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(TopicNames.USER_EVENTS)
                .partitions(3)
                .replicas(1) // Single node setup
                .compact() // Use log compaction for user state events
                .build();
    }

    /**
     * Create notification-events topic for cross-cutting notifications
     */
    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(TopicNames.NOTIFICATION_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Dead letter topic for failed message processing
     */
    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(TopicNames.DEAD_LETTER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}