package com.tandapayinterview.core.kafka;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
    @Autowired
    private KafkaTemplate<String, PaymentRequestPayload> kafkaTemplate;
    private static final String TOPIC = "payment-topic";

    /**
     * publish payment request to payment-topic
     * @param paymentRequestPayload
     */
    public void sendPaymentRequest(PaymentRequestPayload paymentRequestPayload) {
        log.info("Sending Payment request to Integration service = < {} >", paymentRequestPayload);

        kafkaTemplate.send(TOPIC, paymentRequestPayload);
    }
}
