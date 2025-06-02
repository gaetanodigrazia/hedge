package com.leep.security.hedge.tracing.model;

import java.util.Objects;

public class ApiCallEvent {
    private String path;
    private String method;
    private String userId;
    private String remoteIp;
    private String area;
    private Severity severity;
    private String timestamp;
    private long durationMillis;
    private boolean rateLimitNearThreshold;
    private boolean exceptionOccurred;
    private String exceptionName;
    private String rateLimiterType;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public boolean isRateLimitNearThreshold() {
        return rateLimitNearThreshold;
    }

    public void setRateLimitNearThreshold(boolean rateLimitNearThreshold) {
        this.rateLimitNearThreshold = rateLimitNearThreshold;
    }

    public boolean isExceptionOccurred() {
        return exceptionOccurred;
    }

    public void setExceptionOccurred(boolean exceptionOccurred) {
        this.exceptionOccurred = exceptionOccurred;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getRateLimiterType() {
        return rateLimiterType;
    }

    public void setRateLimiterType(String rateLimiterType) {
        this.rateLimiterType = rateLimiterType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApiCallEvent that = (ApiCallEvent) o;
        return durationMillis == that.durationMillis && rateLimitNearThreshold == that.rateLimitNearThreshold && exceptionOccurred == that.exceptionOccurred && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(userId, that.userId) && Objects.equals(remoteIp, that.remoteIp) && Objects.equals(area, that.area) && severity == that.severity && Objects.equals(timestamp, that.timestamp) && Objects.equals(exceptionName, that.exceptionName) && Objects.equals(rateLimiterType, that.rateLimiterType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method, userId, remoteIp, area, severity, timestamp, durationMillis, rateLimitNearThreshold, exceptionOccurred, exceptionName, rateLimiterType);
    }

    @Override
    public String toString() {
        return "ApiCallEvent{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", userId='" + userId + '\'' +
                ", remoteIp='" + remoteIp + '\'' +
                ", area='" + area + '\'' +
                ", severity=" + severity +
                ", timestamp='" + timestamp + '\'' +
                ", durationMillis=" + durationMillis +
                ", rateLimitNearThreshold=" + rateLimitNearThreshold +
                ", exceptionOccurred=" + exceptionOccurred +
                ", exceptionName='" + exceptionName + '\'' +
                ", rateLimiterType='" + rateLimiterType + '\'' +
                '}';
    }
}