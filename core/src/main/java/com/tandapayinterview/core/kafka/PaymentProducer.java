package com.tandapayinterview.core.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
    private final KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    /**
     * publish payment request to payment-topic
     * @param paymentRequest
     */
    public void sendPaymentRequest(PaymentRequest paymentRequest) {
        log.info("Sending Payment request to Integration service");
        Message<PaymentRequest> message = MessageBuilder
                .withPayload(paymentRequest)
                .setHeader(TOPIC, "payment-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
