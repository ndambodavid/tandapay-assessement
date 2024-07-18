package com.tandapayinterview.core.service;

import com.tandapayinterview.core.mapper.PaymentMapper;
import com.tandapayinterview.core.kafka.PaymentProducer;
import com.tandapayinterview.core.model.Payment;
import com.tandapayinterview.core.repository.PaymentRepository;
import com.tandapayinterview.core.request.PaymentRequest;
import com.tandapayinterview.core.response.GatewayResponse;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProducer paymentProducer;

    /**
     * Add Payment
     * @param  request payment request
     * @return id of created payment
     */
    public String createPayment(PaymentRequest request) {
        var payment = paymentRepository.save(paymentMapper.toPayment(request));

        // publish payment request message to payment-topic
        paymentProducer.sendPaymentRequest(
                new com.tandapayinterview.core.kafka.PaymentRequest(
                        payment.getId(),
                        payment.getAmount(),
                        payment.getMobileNumber()
                )
        );

        return payment.getId();
    }

    /**
     * update payment information with information from integration service
     * @param gatewayResponse payment response with information from integration service
     */
    public void updatePayment(GatewayResponse gatewayResponse) {
        var payment = this.paymentRepository.findById(gatewayResponse.getPaymentId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update payment:: No payment found with the provided ID: %s", gatewayResponse.getPaymentId())
                ));
        mergePayment(payment, gatewayResponse);
        paymentRepository.save(payment);
    }

    private void mergePayment(Payment payment, GatewayResponse gatewayResponse) {
        if (StringUtils.isNotBlank(gatewayResponse.getReference())) {
            payment.setReference(gatewayResponse.getReference());
        }

        if (StringUtils.isNotBlank(gatewayResponse.getStatus())) {
            payment.setStatus(gatewayResponse.getStatus());
        }
    }
}
