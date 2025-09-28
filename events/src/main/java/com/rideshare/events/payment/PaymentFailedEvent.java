package com.rideshare.events.payment;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when payment fails for a ride
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent extends BaseEvent {
    
    private UUID paymentId;
    private UUID rideId;
    private UUID userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String failureReason;
    private String errorCode;
    private String gatewayResponse;
    private LocalDateTime failedAt;
    private Boolean retryable;
    
    /**
     * Factory method to create PaymentFailedEvent
     */
    public static PaymentFailedEvent create(UUID paymentId, UUID rideId, UUID userId,
                                          BigDecimal amount, String paymentMethod, String failureReason,
                                          String errorCode, String gatewayResponse, LocalDateTime failedAt,
                                          Boolean retryable, String correlationId) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .paymentId(paymentId)
                .rideId(rideId)
                .userId(userId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .failureReason(failureReason)
                .errorCode(errorCode)
                .gatewayResponse(gatewayResponse)
                .failedAt(failedAt)
                .retryable(retryable)
                .build();
                
        event.setEventType("PAYMENT_FAILED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(paymentId);
        event.setSource("payment-service");
        
        return event;
    }
}