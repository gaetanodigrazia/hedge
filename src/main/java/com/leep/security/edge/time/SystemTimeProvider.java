package com.leep.security.edge.time;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
