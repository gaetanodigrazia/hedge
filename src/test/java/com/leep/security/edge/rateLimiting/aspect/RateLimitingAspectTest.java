package com.leep.security.edge.rateLimiting.aspect;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    String prefix = "/api/public/test";
    @Test
    public void shouldReturnNonRateLimitedResponse() throws Exception {
        mockMvc.perform(get(prefix+"/findAll/notRateLimited"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ciao")));
    }

    @Test
    public void shouldReturnRateLimitedResponseWithinLimit() throws Exception {
        // Should pass within rate limit
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(prefix+"/findAll/rateLimited"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void shouldReturnTooManyRequestsWhenRateLimitExceeded() throws Exception {
        // 3 requests should pass
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(prefix+"/findAll/rateLimited"))
                    .andExpect(status().isOk());
        }

        // 4th should fail due to rate limiting
        mockMvc.perform(get(prefix+"/findAll/rateLimited"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void shouldValidateAndRateLimit() throws Exception {
        // Valid input, within limit
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(prefix+"/findById/validatedAndRateLimited")
                            .param("name", "ShortName"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Limite validato")));
        }

        // Exceeding rate limit
        mockMvc.perform(get(prefix+"/findById/validatedAndRateLimited")
                        .param("name", "ShortName"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void shouldRejectLongNameInput() throws Exception {
        mockMvc.perform(get(prefix+"/findById/validatedAndRateLimited")
                        .param("name", "a".repeat(51)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Limite superato")));
    }
}