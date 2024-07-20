package com.tandapayinterview.integration.kafka;

/**
 * payload to send back to core (CPS)
 * @param paymentId
 * @param reference
 * @param status
 */
public record CoreResponsePayload(
        String paymentId,
        String reference,
        String status
) {
}
