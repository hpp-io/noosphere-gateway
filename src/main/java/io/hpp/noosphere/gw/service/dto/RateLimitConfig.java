package io.hpp.noosphere.gw.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitConfig {
    private String key;
    private long capacity;
    private int refillAmount;
    private int refillTimeInSeconds;
    private int page;
}
