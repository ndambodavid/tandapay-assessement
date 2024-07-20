package com.tandapayinterview.integration.mapper;

import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.request.PaymentRequestPayload;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestMapper {
    public PaymentRequest toPaymentRequest(PaymentRequestPayload request) {
        if (request == null) {
            return null;
        }

        return PaymentRequest.builder()
//                .id(request.id())
                .paymentId(request.paymentId())
                .reference("")
                .amount(request.payableAmount())
                .mobileNumber(request.mobileNumber())
                .status("")
                .build();
    }
}
