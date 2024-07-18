package com.tandapayinterview.core.kafka;

/**
 * payload for payment request
 * @param paymentId
 * @param payableAmount
 * @param mssdn
 */
public record PaymentRequest(
        String paymentId,
        Float payableAmount,
        String mssdn
) {
}
