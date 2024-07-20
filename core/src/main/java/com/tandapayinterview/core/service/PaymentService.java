package com.tandapayinterview.core.service;

import com.tandapayinterview.core.kafka.PaymentRequestPayload;
import com.tandapayinterview.core.mapper.PaymentMapper;
import com.tandapayinterview.core.kafka.PaymentProducer;
import com.tandapayinterview.core.model.Payment;
import com.tandapayinterview.core.repository.PaymentRepository;
import com.tandapayinterview.core.request.PaymentRequest;
import com.tandapayinterview.core.response.ApiResponse;
import com.tandapayinterview.core.response.GatewayResponsePayload;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

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
    public ApiResponse createPayment(PaymentRequest request) {
        var payment = paymentRepository.save(paymentMapper.toPayment(request));

        // publish payment request message to payment-topic
        paymentProducer.sendPaymentRequest(
                new PaymentRequestPayload(
                        payment.getId(),
                        payment.getAmount(),
                        payment.getMobileNumber()
                )
        );

        return new ApiResponse("payment request submitted to integration service", payment.getId());
    }

    /**
     * update payment information with information from integration service
     * @param gatewayResponsePayload payment response with information from integration service
     */
    public void updatePayment(GatewayResponsePayload gatewayResponsePayload) throws Exception {
        var payment = this.paymentRepository.findById(gatewayResponsePayload.getPaymentId())
                .orElseThrow(() -> new Exception(
                        String.format("Cannot update payment:: No payment found with the provided ID: %s", gatewayResponsePayload.getPaymentId())
                ));
        mergePayment(payment, gatewayResponsePayload);
        paymentRepository.save(payment);
    }

    private void mergePayment(Payment payment, GatewayResponsePayload gatewayResponsePayload) {
        if (StringUtils.isNotBlank(gatewayResponsePayload.getReference())) {
            payment.setReference(gatewayResponsePayload.getReference());
        }

        if (StringUtils.isNotBlank(gatewayResponsePayload.getStatus())) {
            payment.setStatus(gatewayResponsePayload.getStatus());
        }
    }
}
