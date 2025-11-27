package io.hpp.noosphere.gw.domain;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "rate_limit_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class RateLimitConfig {

    @Id
    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "calls_per_second")
    private Long callsPerSecond;

    @Column(name = "calls_per_minute")
    private Long callsPerMinute;

    @Column(name = "calls_per_hour")
    private Long callsPerHour;

    @Column(name = "calls_per_day")
    private Long callsPerDay;
}
