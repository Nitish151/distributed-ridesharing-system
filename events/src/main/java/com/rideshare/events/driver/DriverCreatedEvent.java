package com.rideshare.events.driver;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a new driver is created
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverCreatedEvent extends BaseEvent {
    
    private UUID driverId;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String vehicleInfo;
    private String status; // ACTIVE, INACTIVE, PENDING_VERIFICATION
    private LocalDateTime createdAt;
    
    /**
     * Factory method to create DriverCreatedEvent
     */
    public static DriverCreatedEvent create(UUID driverId, String name, String email,
                                          String phone, String licenseNumber, String vehicleInfo,
                                          String status, LocalDateTime createdAt, String correlationId) {
        DriverCreatedEvent event = DriverCreatedEvent.builder()
                .driverId(driverId)
                .name(name)
                .email(email)
                .phone(phone)
                .licenseNumber(licenseNumber)
                .vehicleInfo(vehicleInfo)
                .status(status)
                .createdAt(createdAt)
                .build();
                
        event.setEventType("DRIVER_CREATED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(driverId);
        event.setSource("driver-service");
        
        return event;
    }
}