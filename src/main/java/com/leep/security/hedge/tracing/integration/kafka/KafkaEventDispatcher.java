package com.leep.security.hedge.tracing.integration.kafka;

import com.leep.security.hedge.tracing.model.ApiCallEvent;

public interface KafkaEventDispatcher {
    void send(ApiCallEvent event);
}
