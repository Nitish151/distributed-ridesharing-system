package com.rideshare.events.user;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.util.UUID;

/**
 * Event published when user profile is updated
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdatedEvent extends BaseEvent {
    
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    
    /**
     * Factory method to create UserUpdatedEvent
     */
    public static UserUpdatedEvent create(UUID userId, String name, String email, 
                                         String phone, String role, String correlationId) {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId(userId)
                .name(name)
                .email(email)
                .phone(phone)
                .role(role)
                .build();
                
        event.setEventType("USER_UPDATED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(userId);
        event.setSource("user-service");
        
        return event;
    }
}