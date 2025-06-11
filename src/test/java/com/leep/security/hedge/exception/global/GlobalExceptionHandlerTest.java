package com.leep.security.hedge.exception.global;


import com.leep.security.hedge.exception.model.LengthControlException;
import com.leep.security.hedge.exception.model.OsInjectionDetectedException;
import com.leep.security.hedge.exception.model.SqlInjectionDetectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    public void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    public void testSqlInjectionDetectedException() {
        SqlInjectionDetectedException ex = new SqlInjectionDetectedException("SQL Injection detected");
        ResponseEntity<?> response = handler.handleSqlInjection(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("SQL Injection detected"));
    }

    @Test
    public void testOsInjectionDetectedException() {
        OsInjectionDetectedException ex = new OsInjectionDetectedException("OS Injection attempt");
        ResponseEntity<?> response = handler.handleOsInjection(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("OS Injection attempt"));
    }

    @Test
    public void testLengthControlException() {
        LengthControlException ex = new LengthControlException("Input too long");
        ResponseEntity<?> response = handler.handleLengthControlException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Input too long"));
    }
}
