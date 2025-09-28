package com.rideshare.events.user;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when user is deleted (soft delete)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeletedEvent extends BaseEvent {
    
    private UUID userId;
    private String email;
    private LocalDateTime deletedAt;
    
    /**
     * Factory method to create UserDeletedEvent
     */
    public static UserDeletedEvent create(UUID userId, String email, 
                                         LocalDateTime deletedAt, String correlationId) {
        UserDeletedEvent event = UserDeletedEvent.builder()
                .userId(userId)
                .email(email)
                .deletedAt(deletedAt)
                .build();
                
        event.setEventType("USER_DELETED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(userId);
        event.setSource("user-service");
        
        return event;
    }
}