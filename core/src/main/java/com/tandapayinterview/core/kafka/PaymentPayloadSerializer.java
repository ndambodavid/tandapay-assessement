package com.tandapayinterview.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;


public class PaymentPayloadSerializer implements Serializer<PaymentRequestPayload> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, PaymentRequestPayload data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing PaymentPayload", e);
        }
    }

    @Override
    public void close() {
    }
}
