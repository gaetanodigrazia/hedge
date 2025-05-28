package com.leep.security.edge.tracing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leep.security.edge.tracing.model.ApiCallEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/traces")
public class TraceViewController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public TraceViewController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public List<ApiCallEvent> getTraces() throws JsonProcessingException {
        List<String> jsonList = redisTemplate.opsForList().range("api:traces", 0, -1);
        List<ApiCallEvent> events = new ArrayList<>();
        if (jsonList != null) {
            for (String json : jsonList) {
                events.add(mapper.readValue(json, ApiCallEvent.class));
            }
        }
        return events;
    }
}