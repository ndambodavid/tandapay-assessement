package com.tandapayinterview.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapayinterview.core.response.GatewayResponsePayload;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class GatewayResponseDeserializer implements Deserializer<GatewayResponsePayload> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GatewayResponsePayload deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, GatewayResponsePayload.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing GatewayResponse", e);
        }
    }

    @Override
    public void close() {
    }
}
