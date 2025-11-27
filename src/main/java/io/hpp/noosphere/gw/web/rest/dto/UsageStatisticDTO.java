package io.hpp.noosphere.gw.web.rest.dto;

import java.time.Instant;

public class UsageStatisticDTO {

    private Instant timestamp;
    private String userId;
    private String apiKey;
    private String apiGroup;
    private String endpoint;
    private String method;
    private Integer status;
    private Long duration; // in milliseconds

    // Constructors
    public UsageStatisticDTO() {
    }

    public UsageStatisticDTO(Instant timestamp, String userId, String apiKey, String apiGroup, String endpoint, String method, Integer status, Long duration) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.apiKey = apiKey;
        this.apiGroup = apiGroup;
        this.endpoint = endpoint;
        this.method = method;
        this.status = status;
        this.duration = duration;
    }

    // Getters and Setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "UsageStatisticDTO{" +
               "timestamp=" + timestamp +
               ", userId='" + userId + '\'' +
               ", apiKey='" + apiKey + '\'' +
               ", apiGroup='" + apiGroup + '\'' +
               ", endpoint='" + endpoint + '\'' +
               ", method='" + method + '\'' +
               ", status=" + status +
               ", duration=" + duration +
               '}';
    }
}
