package com.tandapayinterview.core.kafka;


import com.tandapayinterview.core.response.GatewayResponsePayload;
import com.tandapayinterview.core.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayResponseConsumer {

    private final PaymentService paymentService;

    // consume message from gateway-response-topic
    @KafkaListener(topics = "gateway-response-topic", groupId = "payment-group")
    public void consumeGatewayResponse(GatewayResponsePayload gatewayResponsePayload) throws Exception {
        log.info("Consuming the message from gateway-response-topic Topic= < {} >", gatewayResponsePayload);

        // update payment log instance
        paymentService.updatePayment(gatewayResponsePayload);
    }
}
