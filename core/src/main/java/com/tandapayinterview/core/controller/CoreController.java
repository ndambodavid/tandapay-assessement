package com.tandapayinterview.core.controller;

import com.tandapayinterview.core.request.PaymentRequest;
import com.tandapayinterview.core.response.ApiResponse;
import com.tandapayinterview.core.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core")
@RequiredArgsConstructor
public class CoreController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse> makePayment(
            @RequestBody @Valid PaymentRequest request
            ) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }
}
