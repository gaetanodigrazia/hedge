package com.leep.security.hedge.tracing.integration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leep.security.hedge.config.model.KafkaProperties;
import com.leep.security.hedge.exception.model.LengthControlException;
import com.leep.security.hedge.exception.model.RateLimitExceededException;
import com.leep.security.hedge.exception.model.SQLInjectionControlException;
import com.leep.security.hedge.tracing.model.ApiCallEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "hedge.kafka.enabled", havingValue = "true")
public class KafkaApiCallDispatcher implements KafkaEventDispatcher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> topicMappings = new HashMap<>();

    public KafkaApiCallDispatcher(@Qualifier("hedgeKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate, KafkaProperties properties) {

        this.kafkaTemplate = kafkaTemplate;

        KafkaProperties.Topics topics = properties.getTopics();
        topicMappings.put(SQLInjectionControlException.class.getSimpleName(), topics.getSqlInjection());
        topicMappings.put(LengthControlException.class.getSimpleName(), topics.getLengthControl());
        topicMappings.put(RateLimitExceededException.class.getSimpleName(), topics.getRateLimit());
    }

    @Override
    public void send(ApiCallEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String topic = topicMappings.getOrDefault(event.getExceptionName(), "api.generic");
            System.out.println("Mapped topics: " + topicMappings);
            System.out.println("Exception name in event: " + event.getExceptionName());

            kafkaTemplate.send(topic, payload);
        } catch (JsonProcessingException e) {
            System.err.println("[Kafka Dispatcher] Errore serializzazione evento: " + e.getMessage());
        }
    }
}
