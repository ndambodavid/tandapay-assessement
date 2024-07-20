package com.tandapayinterview.integration.service;

import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.repository.PaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RescurringTransactionStatusChecker {

    private final PaymentRequestRepository paymentRequestRepository;
    private final IntegrationService integrationService;

    @Scheduled(fixedRate = 300000) // schedule task to run after every 5 minutes
    public void sendStatusCheckRequestGw() {
        log.info("fetching pending transaction requests from log");
        // fetch all pending transaction requests
        List<PaymentRequest> paymentRequests = paymentRequestRepository.findAll();

        if (paymentRequests.isEmpty()) {
            log.info("No pending payment requests currently");
        } else {
            // get access token from daraja auth api
            integrationService.getAccessToken().subscribe(accessToken -> {
                System.out.println("Access Token: " + accessToken);

                // send check transaction status request for every pending transaction log to gateway
                for (PaymentRequest paymentRequest : paymentRequests) {
                    integrationService.sendCheckTransactionRequestToGw(paymentRequest, accessToken);
                }
            });
        }

    }
}
