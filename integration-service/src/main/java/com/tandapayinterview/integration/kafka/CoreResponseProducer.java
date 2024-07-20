package com.tandapayinterview.integration.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CoreResponseProducer {
    @Autowired
    private KafkaTemplate<String, CoreResponsePayload> kafkaTemplate;
    private static final String TOPIC = "gateway-response-topic";

    /**
     * publish gateway response to gateway-response-topic
     * @param coreResponsePayload
     */
    public void sendGatewayResponseToCore(CoreResponsePayload coreResponsePayload) {
        log.info("Sending gateway response to Core service= < {} >", coreResponsePayload);

        kafkaTemplate.send(TOPIC, coreResponsePayload);
    }
}
