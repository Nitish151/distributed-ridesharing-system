package com.rideshare.events.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideshare.events.common.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for publishing events to Kafka topics
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes an event to the specified Kafka topic
     * 
     * @param topic The Kafka topic name
     * @param event The event to publish
     * @param <T>   The event type extending BaseEvent
     * @return CompletableFuture with send result
     */
    public <T extends BaseEvent> CompletableFuture<SendResult<String, Object>> publishEvent(
            String topic, T event) {

        try {
            log.info("Publishing event: {} to topic: {}", event.getEventType(), topic);
            log.debug("Event details: {}", objectMapper.writeValueAsString(event));

            // Use eventId as the key for proper partitioning
            String key = event.getEventId().toString();

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published event {} to topic {} with offset {}",
                            event.getEventType(), topic, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event {} to topic {}",
                            event.getEventType(), topic, ex);
                }
            });

            return future;

        } catch (Exception e) {
            log.error("Error preparing event for publishing: {}", e.getMessage(), e);
            CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    /**
     * Publishes an event synchronously
     * 
     * @param topic The Kafka topic name
     * @param event The event to publish
     * @param <T>   The event type extending BaseEvent
     * @return SendResult
     * @throws Exception if publishing fails
     */
    public <T extends BaseEvent> SendResult<String, Object> publishEventSync(
            String topic, T event) throws Exception {

        log.info("Publishing event synchronously: {} to topic: {}", event.getEventType(), topic);

        String key = event.getEventId().toString();
        SendResult<String, Object> result = kafkaTemplate.send(topic, key, event).get();

        log.info("Successfully published event {} to topic {} with offset {}",
                event.getEventType(), topic, result.getRecordMetadata().offset());

        return result;
    }
}
