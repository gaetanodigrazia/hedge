package com.leep.security.edge.exception.global;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRateLimitExceeded() throws Exception {

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/findAll/rateLimited"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/findAll/rateLimited"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testValidationFailure() throws Exception {
        mockMvc.perform(get("/findById/validatedAndRateLimited")
                        .param("name", "a".repeat(51)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Limite superato")); // o un tuo messaggio custom
    }

    @Test
    public void testGenericException() throws Exception {
        mockMvc.perform(get("/someInvalidPath"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }
}
