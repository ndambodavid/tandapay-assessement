package com.tandapayinterview.core.kafka;

public record PaymentRequest(
        String paymentId,
        Double payableAmount,
        String mssdn
) {
}
