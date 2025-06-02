package com.leep.security.hedge.config.kafka;

import com.leep.security.hedge.config.model.KafkaProperties;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty(name = "hedge.kafka.enabled", havingValue = "true")
public class HedgeKafkaConfig {

    @Bean(name = "hedgeProducerFactory")
    @ConditionalOnMissingBean(name = "hedgeProducerFactory")
    public ProducerFactory<String, String> hedgeProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean(name = "hedgeKafkaTemplate")
    @ConditionalOnMissingBean(name = "hedgeKafkaTemplate")
    public KafkaTemplate<String, String> hedgeKafkaTemplate(
            @Qualifier("hedgeProducerFactory") ProducerFactory<String, String> hedgeProducerFactory) {
        return new KafkaTemplate<>(hedgeProducerFactory);
    }
}
