package com.rideshare.events.ride;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a ride is completed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideCompletedEvent extends BaseEvent {
    
    private UUID rideId;
    private UUID driverId;
    private UUID userId;
    private BigDecimal actualFare;
    private Integer distanceKm;
    private Integer durationMinutes;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String paymentMethod;
    private String paymentStatus;
    
    /**
     * Factory method to create RideCompletedEvent
     */
    public static RideCompletedEvent create(UUID rideId, UUID driverId, UUID userId,
                                          BigDecimal actualFare, Integer distanceKm, Integer durationMinutes,
                                          String startAddress, String endAddress, LocalDateTime startTime,
                                          LocalDateTime endTime, String paymentMethod, String paymentStatus,
                                          String correlationId) {
        RideCompletedEvent event = RideCompletedEvent.builder()
                .rideId(rideId)
                .driverId(driverId)
                .userId(userId)
                .actualFare(actualFare)
                .distanceKm(distanceKm)
                .durationMinutes(durationMinutes)
                .startAddress(startAddress)
                .endAddress(endAddress)
                .startTime(startTime)
                .endTime(endTime)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .build();
                
        event.setEventType("RIDE_COMPLETED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(rideId);
        event.setSource("ride-service");
        
        return event;
    }
}