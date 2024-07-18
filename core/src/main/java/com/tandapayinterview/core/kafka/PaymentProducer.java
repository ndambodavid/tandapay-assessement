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
    private final KafkaTemplate<String, PaymentRequestPayload> kafkaTemplate;

    /**
     * publish payment request to payment-topic
     * @param paymentRequestPayload
     */
    public void sendPaymentRequest(PaymentRequestPayload paymentRequestPayload) {
        log.info("Sending Payment request to Integration service = < {} >", paymentRequestPayload);
        Message<PaymentRequestPayload> message = MessageBuilder
                .withPayload(paymentRequestPayload)
                .setHeader(TOPIC, "payment-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
