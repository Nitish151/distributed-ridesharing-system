package com.rideshare.events.ride;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a ride is requested by a user
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestedEvent extends BaseEvent {
    
    private UUID rideId;
    private UUID userId;
    private String pickupAddress;
    private String destinationAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private BigDecimal estimatedFare;
    private String rideType; // STANDARD, PREMIUM, SHARED
    private LocalDateTime requestedAt;
    
    /**
     * Factory method to create RideRequestedEvent
     */
    public static RideRequestedEvent create(UUID rideId, UUID userId, String pickupAddress,
                                          String destinationAddress, Double pickupLatitude,
                                          Double pickupLongitude, Double destinationLatitude,
                                          Double destinationLongitude, BigDecimal estimatedFare,
                                          String rideType, LocalDateTime requestedAt, String correlationId) {
        RideRequestedEvent event = RideRequestedEvent.builder()
                .rideId(rideId)
                .userId(userId)
                .pickupAddress(pickupAddress)
                .destinationAddress(destinationAddress)
                .pickupLatitude(pickupLatitude)
                .pickupLongitude(pickupLongitude)
                .destinationLatitude(destinationLatitude)
                .destinationLongitude(destinationLongitude)
                .estimatedFare(estimatedFare)
                .rideType(rideType)
                .requestedAt(requestedAt)
                .build();
                
        event.setEventType("RIDE_REQUESTED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(rideId);
        event.setSource("ride-service");
        
        return event;
    }
}