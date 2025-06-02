package com.leep.security.hedge.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(name = "hedge.security.enabled", havingValue = "true", matchIfMissing = false)
public class NoSecurityConfig {

    public NoSecurityConfig() {
        System.out.println("NoSecurityConfig ATTIVO");
    }

    @Bean
    @Order(2)
    public SecurityFilterChain hedgeApiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/hedge/**")
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().denyAll()
                );
        return http.build();
    }

}
