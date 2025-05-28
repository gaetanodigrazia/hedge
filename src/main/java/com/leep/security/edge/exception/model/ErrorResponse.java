package com.leep.security.edge.exception.model;

import java.util.Objects;

/**
 * Standard structure for error responses returned by the API.
 * Contains status code, message, timestamp, and optional request metadata.
 */
public class ErrorResponse {

    private int status;
    private String message;
    private String timestamp;

    private String path;
    private String method;
    private String userAgent;
    private String remoteIp;
    private String userId;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }


    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getRemoteIp() { return remoteIp; }
    public void setRemoteIp(String remoteIp) { this.remoteIp = remoteIp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return status == that.status &&
                Objects.equals(message, that.message) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(path, that.path) &&
                Objects.equals(method, that.method) &&
                Objects.equals(userAgent, that.userAgent) &&
                Objects.equals(remoteIp, that.remoteIp) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, timestamp, path, method, userAgent, remoteIp, userId);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", remoteIp='" + remoteIp + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
