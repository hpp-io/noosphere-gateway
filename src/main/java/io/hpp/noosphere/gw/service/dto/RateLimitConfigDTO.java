package io.hpp.noosphere.gw.service.dto;

public class RateLimitConfigDTO {

    private String apiKey;
    private Long callsPerSecond;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getCallsPerSecond() {
        return callsPerSecond;
    }

    public void setCallsPerSecond(Long callsPerSecond) {
        this.callsPerSecond = callsPerSecond;
    }
}
