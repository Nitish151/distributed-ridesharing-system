package com.rideshare.user.service;

import com.rideshare.events.config.TopicNames;
import com.rideshare.events.user.UserCreatedEvent;
import com.rideshare.events.user.UserUpdatedEvent;
import com.rideshare.events.user.UserDeletedEvent;
import com.rideshare.events.util.EventPublisher;
import com.rideshare.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for publishing user-related events to Kafka
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final EventPublisher eventPublisher;

    /**
     * Publishes UserCreatedEvent when a new user is registered
     */
    public void publishUserCreated(User user, String correlationId) {
        try {
            UserCreatedEvent event = UserCreatedEvent.create(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().toString(),
                    correlationId != null ? correlationId : UUID.randomUUID().toString()
            );

            eventPublisher.publishEvent(TopicNames.USER_EVENTS, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("UserCreatedEvent published for user: {}", user.getId());
                        } else {
                            log.error("Failed to publish UserCreatedEvent for user: {}", 
                                    user.getId(), ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error creating UserCreatedEvent for user: {}", user.getId(), e);
        }
    }

    /**
     * Publishes UserUpdatedEvent when user data is modified
     */
    public void publishUserUpdated(User user, User previousUser, String correlationId) {
        try {
            UserUpdatedEvent event = UserUpdatedEvent.create(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().toString(),
                    correlationId != null ? correlationId : UUID.randomUUID().toString()
            );

            eventPublisher.publishEvent(TopicNames.USER_EVENTS, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("UserUpdatedEvent published for user: {}", user.getId());
                        } else {
                            log.error("Failed to publish UserUpdatedEvent for user: {}", 
                                    user.getId(), ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error creating UserUpdatedEvent for user: {}", user.getId(), e);
        }
    }

    /**
     * Publishes UserDeletedEvent when user is deleted/deactivated
     */
    public void publishUserDeleted(UUID userId, String email, String correlationId) {
        try {
            UserDeletedEvent event = UserDeletedEvent.create(
                    userId,
                    email,
                    LocalDateTime.now(),
                    correlationId != null ? correlationId : UUID.randomUUID().toString()
            );

            eventPublisher.publishEvent(TopicNames.USER_EVENTS, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("UserDeletedEvent published for user: {}", userId);
                        } else {
                            log.error("Failed to publish UserDeletedEvent for user: {}", 
                                    userId, ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error creating UserDeletedEvent for user: {}", userId, e);
        }
    }

    /**
     * Publishes UserCreatedEvent synchronously (for critical operations)
     */
    public void publishUserCreatedSync(User user, String correlationId) throws Exception {
        UserCreatedEvent event = UserCreatedEvent.create(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().toString(),
                correlationId != null ? correlationId : UUID.randomUUID().toString()
        );

        eventPublisher.publishEventSync(TopicNames.USER_EVENTS, event);
        log.info("UserCreatedEvent published synchronously for user: {}", user.getId());
    }
}