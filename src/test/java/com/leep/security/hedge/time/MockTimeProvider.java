package com.leep.security.hedge.time;

public class MockTimeProvider implements TimeProvider {

    private long currentTime;

    public MockTimeProvider(long startTime) {
        this.currentTime = startTime;
    }

    @Override
    public long currentTimeMillis() {
        return currentTime;
    }

    public void advanceMillis(long millis) {
        this.currentTime += millis;
    }

    public void advanceSeconds(long seconds) {
        this.currentTime += seconds * 1000;
    }
}
