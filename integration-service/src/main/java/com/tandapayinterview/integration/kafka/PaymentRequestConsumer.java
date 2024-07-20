package com.tandapayinterview.integration.kafka;

import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.repository.PaymentRequestRepository;
import com.tandapayinterview.integration.request.PaymentRequestPayload;
import com.tandapayinterview.integration.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final IntegrationService integrationService;
    private final PaymentRequestRepository paymentRequestRepository;

    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void consumePaymentRequest(PaymentRequestPayload paymentRequestPayload) {
        log.info("Consuming payment request with payload = < {} >", paymentRequestPayload);

        // log payment request instance
        var paymentRequest = paymentRequestRepository.save(
                PaymentRequest.builder()
                        .paymentId(paymentRequestPayload.paymentId())
                        .amount(paymentRequestPayload.payableAmount())
                        .mobileNumber(paymentRequestPayload.mobileNumber())
                        .status("pending")
                        .reference("")
                        .build()
        );

        // submit payment request to GW
        integrationService.sendPaymentRequestToGw(paymentRequest);
    }
}
