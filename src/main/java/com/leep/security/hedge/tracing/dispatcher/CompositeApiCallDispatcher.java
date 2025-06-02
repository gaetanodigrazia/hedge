package com.leep.security.hedge.tracing.dispatcher;

import com.leep.security.hedge.tracing.dispatcher.ApiCallEventDispatcher;
import com.leep.security.hedge.tracing.integration.kafka.KafkaEventDispatcher;
import com.leep.security.hedge.tracing.integration.redis.RedisApiCallDispatcher;
import com.leep.security.hedge.tracing.model.ApiCallEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CompositeApiCallDispatcher implements ApiCallEventDispatcher {

    private final RedisApiCallDispatcher redisDispatcher;
    private final KafkaEventDispatcher kafkaDispatcher; // può essere null

    @Autowired
    public CompositeApiCallDispatcher(
            RedisApiCallDispatcher redisDispatcher,
            @Autowired(required = false) KafkaEventDispatcher kafkaDispatcher
    ) {
        this.redisDispatcher = redisDispatcher;
        this.kafkaDispatcher = kafkaDispatcher;
    }

    @Override
    public void dispatch(ApiCallEvent event) {
        if (redisDispatcher != null) {
            redisDispatcher.dispatch(event);
        }

        if (kafkaDispatcher != null) {
            kafkaDispatcher.send(event);
        }
    }
}
