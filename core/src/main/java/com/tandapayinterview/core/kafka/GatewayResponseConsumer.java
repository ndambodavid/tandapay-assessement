package com.tandapayinterview.core.kafka;


import com.tandapayinterview.core.response.GatewayResponse;
import com.tandapayinterview.core.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayResponseConsumer {

    private final PaymentService paymentService;

    // consume message from gateway-response-topic
    @KafkaListener(topics = "gateway-response-topic")
    public void consumePaymentRequest(GatewayResponse gatewayResponse) throws Exception {
        log.info(format("Consuming the message from gateway-response-topic Topic:: %s", gatewayResponse));

        // update payment log instance
        paymentService.updatePayment(gatewayResponse);
    }
}
