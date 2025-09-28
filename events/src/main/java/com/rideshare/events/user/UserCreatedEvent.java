package com.rideshare.events.user;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.util.UUID;

/**
 * Event published when a new user registers in the system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent extends BaseEvent {
    
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String role; // RIDER, DRIVER, ADMIN
    
    /**
     * Factory method to create UserCreatedEvent
     */
    public static UserCreatedEvent create(UUID userId, String name, String email, 
                                         String phone, String role, String correlationId) {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(userId)
                .name(name)
                .email(email)
                .phone(phone)
                .role(role)
                .build();
                
        event.setEventType("USER_CREATED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(userId);
        event.setSource("user-service");
        
        return event;
    }
}