package com.tandapayinterview.integration.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaGatewayResponseTopicConfig {
    // payment topic builder for communicating with integration service
    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder
                .name("gateway-response-topic")
                .build();
    }
}
