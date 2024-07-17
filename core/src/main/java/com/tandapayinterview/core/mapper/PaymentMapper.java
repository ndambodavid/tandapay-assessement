package com.tandapayinterview.core.mapper;

import com.tandapayinterview.core.model.Payment;
import com.tandapayinterview.core.request.PaymentRequest;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {
    public Payment toPayment(PaymentRequest request) {
        if (request == null) {
            return null;
        }
        return Payment.builder()
                .id(request.id())
                .reference(request.reference())
                .amount(request.amount())
                .mobileNumber(request.mobileNumber())
                .currency(request.currency())
                .status(request.status())
                .build();
    }
}
