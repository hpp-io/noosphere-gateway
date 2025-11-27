package io.hpp.noosphere.gw.domain;

import lombok.Data;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "usage_statistic")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class UsageStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "api_group")
    private String apiGroup;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "method")
    private String method;

    @Column(name = "status")
    private Integer status;

    @Column(name = "duration")
    private Long duration;
}
