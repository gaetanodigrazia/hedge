package com.leep.security.edge.rateLimiting.model;

import com.leep.security.edge.time.MockTimeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FixedWindowRateLimiterTest {

    @Test
    void shouldAllowRequestsWithinLimitInSameWindow() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(5, 10);
        String key = "test-user";
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest(key), "Request " + (i + 1) + " should be allowed");
        }
        assertFalse(limiter.allowRequest(key), "Request 6 should be blocked");
    }

    @Test
    void shouldResetLimitAfterWindowPasses() {
        MockTimeProvider time = new MockTimeProvider(0);
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 1, time);
        String key = "user";

        assertTrue(limiter.allowRequest(key));
        assertTrue(limiter.allowRequest(key));
        assertFalse(limiter.allowRequest(key));

        time.advanceSeconds(1); // avanza di 1s ⇒ nuova finestra

        assertTrue(limiter.allowRequest(key));
        assertTrue(limiter.allowRequest(key));
        assertFalse(limiter.allowRequest(key));
    }


    @Test
    void shouldTrackEachKeySeparately() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 10);

        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertFalse(limiter.allowRequest("user1")); // superato

        assertTrue(limiter.allowRequest("user2"));
        assertTrue(limiter.allowRequest("user2"));
        assertFalse(limiter.allowRequest("user2")); // superato
    }
    @Test
    void shouldHandleConcurrentAccessSafely() throws InterruptedException {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(100, 10);
        String key = "user-concurrent";

        int threadCount = 10;
        int requestsPerThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger accepted = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    if (limiter.allowRequest(key)) {
                        accepted.incrementAndGet();
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(100, accepted.get(), "Should accept exactly 100 requests total");
    }
    @Test
    void shouldRejectAllWhenLimitIsZero() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(0, 10);
        assertFalse(limiter.allowRequest("user1"));
    }

    @Test
    void shouldThrowWhenWindowIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FixedWindowRateLimiter(5, 0, new MockTimeProvider(0));
        });
    }


}
