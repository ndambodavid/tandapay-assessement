package com.tandapayinterview.integration.repository;

import com.tandapayinterview.integration.model.PaymentRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRequestRepository extends MongoRepository<PaymentRequest, String> {
}
