package com.rideshare.events.ride;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a driver accepts a ride request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideAcceptedEvent extends BaseEvent {
    
    private UUID rideId;
    private UUID driverId;
    private UUID userId;
    private String driverName;
    private String driverPhone;
    private String vehicleInfo;
    private Double driverLatitude;
    private Double driverLongitude;
    private Integer estimatedArrivalMinutes;
    private LocalDateTime acceptedAt;
    
    /**
     * Factory method to create RideAcceptedEvent
     */
    public static RideAcceptedEvent create(UUID rideId, UUID driverId, UUID userId,
                                         String driverName, String driverPhone, String vehicleInfo,
                                         Double driverLatitude, Double driverLongitude,
                                         Integer estimatedArrivalMinutes, LocalDateTime acceptedAt,
                                         String correlationId) {
        RideAcceptedEvent event = RideAcceptedEvent.builder()
                .rideId(rideId)
                .driverId(driverId)
                .userId(userId)
                .driverName(driverName)
                .driverPhone(driverPhone)
                .vehicleInfo(vehicleInfo)
                .driverLatitude(driverLatitude)
                .driverLongitude(driverLongitude)
                .estimatedArrivalMinutes(estimatedArrivalMinutes)
                .acceptedAt(acceptedAt)
                .build();
                
        event.setEventType("RIDE_ACCEPTED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(rideId);
        event.setSource("matching-service");
        
        return event;
    }
}