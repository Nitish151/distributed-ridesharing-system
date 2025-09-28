package com.rideshare.events.config;

/**
 * Constants for Kafka topic names used across the ride-sharing system
 */
public final class TopicNames {
    
    // User Events
    public static final String USER_EVENTS = "user-events";
    
    // Driver Events  
    public static final String DRIVER_EVENTS = "driver-events";
    
    // Ride Events
    public static final String RIDE_EVENTS = "ride-events";
    
    // Payment Events
    public static final String PAYMENT_EVENTS = "payment-events";
    
    // Notification Events (for cross-cutting concerns)
    public static final String NOTIFICATION_EVENTS = "notification-events";
    
    // Dead Letter Topic (for failed message processing)
    public static final String DEAD_LETTER_TOPIC = "dead-letter-topic";
    
    private TopicNames() {
        // Utility class - prevent instantiation
    }
}