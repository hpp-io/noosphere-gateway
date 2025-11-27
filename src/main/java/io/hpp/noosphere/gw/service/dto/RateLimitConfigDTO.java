package io.hpp.noosphere.gw.service.dto;

import lombok.Data;

@Data
public class RateLimitConfigDTO {

    private String apiKey;
    private Long callsPerSecond;
    private Long callsPerMinute;
    private Long callsPerHour;
    private Long callsPerDay;
}
