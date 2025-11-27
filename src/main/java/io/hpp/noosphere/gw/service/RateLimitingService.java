package io.hpp.noosphere.gw.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.hpp.noosphere.gw.domain.RateLimitConfig;
import io.hpp.noosphere.gw.repository.RateLimitConfigRepository;
import io.hpp.noosphere.gw.service.dto.RateLimitConfigDTO;
import io.hpp.noosphere.gw.service.mapper.RateLimitConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class RateLimitingService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final RateLimitConfigRepository rateLimitConfigRepository;
    private final RateLimitConfigMapper rateLimitConfigMapper;

    public RateLimitingService(
        RateLimitConfigRepository rateLimitConfigRepository,
        RateLimitConfigMapper rateLimitConfigMapper
    ) {
        this.rateLimitConfigRepository = rateLimitConfigRepository;
        this.rateLimitConfigMapper = rateLimitConfigMapper;
    }

    public Mono<Bucket> resolveBucket(String apiKey) {
        if (cache.containsKey(apiKey)) {
            return Mono.just(cache.get(apiKey));
        }

        return Mono.fromCallable(() -> rateLimitConfigRepository.findById(apiKey))
            .flatMap(optionalConfig -> optionalConfig.map(Mono::just).orElseGet(Mono::empty))
            .map(this::newBucket)
            .doOnNext(bucket -> cache.put(apiKey, bucket))
            .switchIfEmpty(Mono.fromCallable(() -> newBucket(createDefaultConfig(apiKey))));
    }

    private Bucket newBucket(RateLimitConfig config) {
        LocalBucketBuilder builder = new LocalBucketBuilder();
        if (config.getCallsPerSecond() != null) {
            builder.addLimit(Bandwidth.classic(config.getCallsPerSecond(), Refill.greedy(config.getCallsPerSecond(), Duration.ofSeconds(1))));
        }
        if (config.getCallsPerMinute() != null) {
            builder.addLimit(Bandwidth.classic(config.getCallsPerMinute(), Refill.greedy(config.getCallsPerMinute(), Duration.ofMinutes(1))));
        }
        if (config.getCallsPerHour() != null) {
            builder.addLimit(Bandwidth.classic(config.getCallsPerHour(), Refill.greedy(config.getCallsPerHour(), Duration.ofHours(1))));
        }
        if (config.getCallsPerDay() != null) {
            builder.addLimit(Bandwidth.classic(config.getCallsPerDay(), Refill.greedy(config.getCallsPerDay(), Duration.ofDays(1))));
        }
        return builder.build();
    }

    private RateLimitConfig createDefaultConfig(String apiKey) {
        RateLimitConfig defaultConfig = new RateLimitConfig();
        defaultConfig.setApiKey(apiKey);
        defaultConfig.setCallsPerSecond(10L); // Default to 10 calls per second
        defaultConfig.setCallsPerMinute(100L); // Default to 100 calls per minute
        defaultConfig.setCallsPerHour(1000L); // Default to 1000 calls per hour
        defaultConfig.setCallsPerDay(10000L); // Default to 10000 calls per day
        return defaultConfig;
    }

    public Mono<Void> updateRateLimit(String apiKey, RateLimitConfigDTO configDTO) {
        RateLimitConfig config = rateLimitConfigMapper.toEntity(configDTO);
        config.setApiKey(apiKey);
        rateLimitConfigRepository.save(config);
        cache.put(apiKey, newBucket(config));
        return Mono.empty();
    }

    public Mono<Void> reloadRateLimit(String apiKey) {
        return Mono.fromCallable(() -> rateLimitConfigRepository.findById(apiKey))
            .flatMap(optionalConfig -> optionalConfig.map(Mono::just).orElseGet(Mono::empty))
            .map(this::newBucket)
            .doOnNext(bucket -> cache.put(apiKey, bucket))
            .then();
    }

    public Map<String, Bucket> getCache() {
        return cache;
    }
}
