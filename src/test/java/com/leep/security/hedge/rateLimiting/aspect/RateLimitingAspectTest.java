package com.leep.security.hedge.rateLimiting.aspect;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Disabled
class RateLimitingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    String prefix = "/api/public/test";
    @Disabled
    @Test
    public void shouldReturnNonRateLimitedResponse() throws Exception {
        mockMvc.perform(get(prefix+"/findAll/notRateLimited"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World")));
    }

    @Test
    @Disabled
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
}