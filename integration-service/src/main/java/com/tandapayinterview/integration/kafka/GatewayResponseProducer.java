package com.tandapayinterview.integration.kafka;

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
public class GatewayResponseProducer {
    private final KafkaTemplate<String, CoreResponse> kafkaTemplate;

    public void sendGatewayResponseToCore(CoreResponse coreResponse) {
        log.info("Sending gateway response to Core service");
        Message<CoreResponse> message = MessageBuilder
                .withPayload(coreResponse)
                .setHeader(TOPIC, "gateway-response-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
