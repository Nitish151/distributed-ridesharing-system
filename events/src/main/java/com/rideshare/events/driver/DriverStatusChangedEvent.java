package com.rideshare.events.driver;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when driver status changes (AVAILABLE, BUSY, OFFLINE)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverStatusChangedEvent extends BaseEvent {
    
    private UUID driverId;
    private String previousStatus;
    private String newStatus;
    private Double latitude;
    private Double longitude;
    private LocalDateTime statusChangedAt;
    
    /**
     * Factory method to create DriverStatusChangedEvent
     */
    public static DriverStatusChangedEvent create(UUID driverId, String previousStatus,
                                                String newStatus, Double latitude, Double longitude,
                                                LocalDateTime statusChangedAt, String correlationId) {
        DriverStatusChangedEvent event = DriverStatusChangedEvent.builder()
                .driverId(driverId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .latitude(latitude)
                .longitude(longitude)
                .statusChangedAt(statusChangedAt)
                .build();
                
        event.setEventType("DRIVER_STATUS_CHANGED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(driverId);
        event.setSource("driver-service");
        
        return event;
    }
}