package com.leep.security.hedge.tracing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leep.security.hedge.tracing.model.ApiCallEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/traces")
public class TraceArchiveController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public TraceArchiveController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/archive")
    public ResponseEntity<Void> archiveTraces(@RequestBody List<ApiCallEvent> eventsToArchive) {
        for (ApiCallEvent event : eventsToArchive) {
            try {
                String json = mapper.writeValueAsString(event);
                redisTemplate.opsForList().remove("api:traces", 1, json);
                // eventualmente puoi pusharlo in un'altra lista per archivio: redisTemplate.opsForList().leftPush("api:archive", json);
            } catch (JsonProcessingException e) {
                // Log error
            }
        }
        return ResponseEntity.ok().build();
    }



    @GetMapping("/archived")
    public List<ApiCallEvent> getArchivedTraces() throws JsonProcessingException {
        List<String> jsonList = redisTemplate.opsForList().range("api:traces:archived", 0, -1);
        List<ApiCallEvent> events = new ArrayList<>();
        if (jsonList != null) {
            for (String json : jsonList) {
                events.add(mapper.readValue(json, ApiCallEvent.class));
            }
        }
        return events;
    }
}
