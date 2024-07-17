package com.tandapayinterview.core.repository;

import com.tandapayinterview.core.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
}
