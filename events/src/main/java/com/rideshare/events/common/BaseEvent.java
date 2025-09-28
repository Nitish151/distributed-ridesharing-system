package com.rideshare.events.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all events in the ride-sharing system
 * Provides common fields and structure for event-driven architecture
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {
    
    /**
     * Unique identifier for this event instance
     */
    private String eventId = UUID.randomUUID().toString();
    
    /**
     * Type of the event (e.g., USER_CREATED, RIDE_REQUESTED)
     */
    private String eventType;
    
    /**
     * Version of the event schema (for backward compatibility)
     */
    private Integer version = 1;
    
    /**
     * Timestamp when the event occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Correlation ID to track related events across services
     */
    private String correlationId;
    
    /**
     * Service that published this event
     */
    private String source;
    
    /**
     * ID of the aggregate root that this event relates to
     */
    private UUID aggregateId;
    
    /**
     * Optional metadata for additional context
     */
    private String metadata;
}