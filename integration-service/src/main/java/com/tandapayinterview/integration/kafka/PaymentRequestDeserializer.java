package com.tandapayinterview.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapayinterview.integration.request.PaymentRequestPayload;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class PaymentRequestDeserializer implements Deserializer<PaymentRequestPayload> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PaymentRequestPayload deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, PaymentRequestPayload.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing PaymentRequest", e);
        }
    }

    @Override
    public void close() {
    }
}
