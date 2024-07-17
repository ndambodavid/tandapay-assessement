package com.tandapayinterview.integration.kafka;

import com.tandapayinterview.integration.mapper.PaymentRequestMapper;
import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.repository.PaymentRequestRepository;
import com.tandapayinterview.integration.request.PaymentRequestGw;
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
    private final PaymentRequestMapper paymentRequestMapper;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentRequest(PaymentRequestGw paymentRequestGw) {
        log.info(format("Consuming the message from payment-topic Topic:: %s", paymentRequestGw));

        // log payment request instance
        var paymentRequest = paymentRequestRepository.save(
                PaymentRequest.builder()
                        .paymentId(paymentRequestGw.paymentId())
                        .amount(paymentRequestGw.amount())
                        .mobileNumber(paymentRequestGw.mobileNumber())
                        .status("pending")
                        .reference("")
                        .build()
        );

        // submit payment request to GW
        integrationService.sendPaymentRequestToGw(paymentRequest);
    }
}
