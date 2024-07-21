package com.tandapayinterview.integration.repository;

import com.tandapayinterview.integration.model.PaymentRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRequestRepository extends MongoRepository<PaymentRequest, String> {
    Optional<PaymentRequest> findByPaymentId(String originatorConversationID);
}
