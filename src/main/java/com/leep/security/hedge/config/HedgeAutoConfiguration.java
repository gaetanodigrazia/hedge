package com.leep.security.hedge.config;

import com.leep.security.hedge.config.model.KafkaProperties;
import com.leep.security.hedge.config.model.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RedisProperties.class, KafkaProperties.class})
@ComponentScan(basePackages = "com.leep.security.hedge")
public class HedgeAutoConfiguration {
}
