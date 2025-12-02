package io.hpp.noosphere.gw.web.rest.dto;

import io.github.bucket4j.Bucket;
import io.hpp.noosphere.gw.service.dto.RateLimitConfigDTO;
import java.io.Serializable;
import lombok.Data;

@Data
public class BucketStateDTO implements Serializable {

  private String apiKey;
  private Long availableTokens;
  private Long callsPerSecond;

  private Long callsPerMinute;

  private Long callsPerHour;

  private Long callsPerDay;

  public BucketStateDTO(String apiKey, RateLimitConfigDTO rateLimitConfig, Bucket bucket) {
    this.apiKey = apiKey;
    this.availableTokens = bucket.getAvailableTokens();
    this.callsPerSecond = rateLimitConfig.getCallsPerSecond();
    this.callsPerMinute = rateLimitConfig.getCallsPerMinute();
    this.callsPerHour = rateLimitConfig.getCallsPerHour();
    this.callsPerDay = rateLimitConfig.getCallsPerDay();
  }

}
