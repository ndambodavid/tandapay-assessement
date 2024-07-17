package com.tandapayinterview.integration.mapper;

import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.request.PaymentRequestGw;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestMapper {
    public PaymentRequest toPaymentRequest(PaymentRequestGw request) {
        if (request == null) {
            return null;
        }

        return PaymentRequest.builder()
                .id(request.id())
                .paymentId(request.paymentId())
                .reference(request.reference())
                .amount(request.amount())
                .mobileNumber(request.mobileNumber())
                .status(request.status())
                .build();
    }
}
