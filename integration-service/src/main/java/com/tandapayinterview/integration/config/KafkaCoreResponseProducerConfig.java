package com.tandapayinterview.integration.config;

import com.tandapayinterview.integration.kafka.CoreResponsePayload;
import com.tandapayinterview.integration.kafka.ResponsePayloadSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaCoreResponseProducerConfig {
    // response topic builder for communicating with core service

    @Bean
    public ProducerFactory<String, CoreResponsePayload> producerFactory() {
        Map<String,Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ResponsePayloadSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, CoreResponsePayload> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
