package com.leep.security.edge.tracing.integration.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leep.security.edge.tracing.dispatcher.ApiCallEventDispatcher;
import com.leep.security.edge.tracing.model.ApiCallEvent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
public class RedisApiCallDispatcher implements ApiCallEventDispatcher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisApiCallDispatcher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void dispatch(ApiCallEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.opsForList().leftPush("api:traces", json);
            redisTemplate.opsForList().trim("api:traces", 0, 99);
            System.err.println("EVENTO OK " + event);

        } catch (JsonProcessingException e) {
            System.err.println("[DISPATCH ERROR] Failed to serialize: " + event);
            e.printStackTrace();
        }
    }
}