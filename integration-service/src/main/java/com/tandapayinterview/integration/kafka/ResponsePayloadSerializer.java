package com.tandapayinterview.integration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ResponsePayloadSerializer implements Serializer<CoreResponsePayload> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, CoreResponsePayload data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing CoreResponsePayload", e);
        }
    }

    @Override
    public void close() {
    }
}
