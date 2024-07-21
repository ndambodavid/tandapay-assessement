package com.tandapayinterview.integration.controller;

import com.tandapayinterview.integration.response.AsyncGwResponse;
import com.tandapayinterview.integration.response.CheckTransactionStatusResponse;
import com.tandapayinterview.integration.service.IntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationService integrationService;

    @PostMapping("/result/callback")
    public void handleGatewayPaymentResultCallback(
            @RequestBody @Valid AsyncGwResponse asyncGwResponse
    ) throws Exception {
        integrationService.handlePaymentRequestResponse(asyncGwResponse);
    }

    @PostMapping("/status/callback")
    public void handleGatewayTransactionStatusCallback(
            @RequestBody @Valid CheckTransactionStatusResponse transactionStatusResponse
            ) throws Exception {
        integrationService.handleTransactionStatusResponse(transactionStatusResponse);
    }
}
