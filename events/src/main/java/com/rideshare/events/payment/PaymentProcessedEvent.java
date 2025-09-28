package com.rideshare.events.payment;

import com.rideshare.events.common.BaseEvent;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when payment is processed for a ride
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProcessedEvent extends BaseEvent {
    
    private UUID paymentId;
    private UUID rideId;
    private UUID userId;
    private UUID driverId;
    private BigDecimal amount;
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, WALLET, CASH
    private String paymentStatus; // SUCCESS, FAILED, PENDING
    private String transactionId;
    private String gatewayResponse;
    private LocalDateTime processedAt;
    
    /**
     * Factory method to create PaymentProcessedEvent
     */
    public static PaymentProcessedEvent create(UUID paymentId, UUID rideId, UUID userId,
                                             UUID driverId, BigDecimal amount, String paymentMethod,
                                             String paymentStatus, String transactionId,
                                             String gatewayResponse, LocalDateTime processedAt,
                                             String correlationId) {
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(paymentId)
                .rideId(rideId)
                .userId(userId)
                .driverId(driverId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .transactionId(transactionId)
                .gatewayResponse(gatewayResponse)
                .processedAt(processedAt)
                .build();
                
        event.setEventType("PAYMENT_PROCESSED");
        event.setCorrelationId(correlationId);
        event.setAggregateId(paymentId);
        event.setSource("payment-service");
        
        return event;
    }
}