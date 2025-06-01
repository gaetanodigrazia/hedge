package com.leep.security.edge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class NoSecurityConfig {

    @Bean
    public SecurityFilterChain hedgeApiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/hedge/**")
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()
                );
        return http.build();
    }

}
