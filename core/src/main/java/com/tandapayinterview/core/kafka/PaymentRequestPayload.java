package com.tandapayinterview.core.kafka;

/**
 * payload for payment request
 * @param paymentId
 * @param payableAmount
 * @param mobileNumber
 */
public record PaymentRequestPayload(
        String paymentId,
        Float payableAmount,
        String mobileNumber
) {
}
