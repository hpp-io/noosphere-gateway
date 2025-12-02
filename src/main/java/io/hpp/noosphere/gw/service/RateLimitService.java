package io.hpp.noosphere.gw.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.hpp.noosphere.gw.domain.RateLimitConfig;
import io.hpp.noosphere.gw.repository.RateLimitConfigRepository;
import io.hpp.noosphere.gw.service.dto.RateLimitConfigDTO;
import io.hpp.noosphere.gw.service.mapper.RateLimitConfigMapper;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
public class RateLimitService {

  @Getter
  private final Map<String, Pair<RateLimitConfigDTO, Bucket>> cache = new ConcurrentHashMap<>();

  private final RateLimitConfigRepository rateLimitConfigRepository;
  private final RateLimitConfigMapper rateLimitConfigMapper;

  public RateLimitService(
    RateLimitConfigRepository rateLimitConfigRepository,
    RateLimitConfigMapper rateLimitConfigMapper
  ) {
    this.rateLimitConfigRepository = rateLimitConfigRepository;
    this.rateLimitConfigMapper = rateLimitConfigMapper;
  }

  public Mono<Pair<RateLimitConfigDTO, Bucket>> resolveBucket(String apiKey) {
    if (cache.containsKey(apiKey)) {
      return Mono.just(cache.get(apiKey));
    }

    return Mono.fromCallable(() -> rateLimitConfigRepository.findById(apiKey))
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap(optionalConfig -> optionalConfig.map(Mono::just).orElseGet(Mono::empty))
      .switchIfEmpty(Mono.defer(() -> {
          RateLimitConfig defaultConfig = createDefaultConfig(apiKey);
          return Mono.fromCallable(() -> rateLimitConfigRepository.save(defaultConfig))
                .subscribeOn(Schedulers.boundedElastic());
      }))
      .map(this::newBucket)
      .doOnNext(pair -> cache.put(apiKey, pair));
  }

  private Pair<RateLimitConfigDTO, Bucket> newBucket(RateLimitConfig config) {
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
    return Pair.of(rateLimitConfigMapper.toDto(config), builder.build());
  }

  public Mono<RateLimitConfigDTO> saveRateLimit(String apiKey, RateLimitConfigDTO configDTO) {
    RateLimitConfig config = rateLimitConfigMapper.toEntity(configDTO);
    config.setApiKey(apiKey);
    return Mono.fromCallable(() -> rateLimitConfigRepository.save(config))
        .subscribeOn(Schedulers.boundedElastic())
        .map(savedConfig -> {
            cache.put(apiKey, newBucket(savedConfig));
            return rateLimitConfigMapper.toDto(savedConfig);
        });
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
    return Mono.fromCallable(() -> rateLimitConfigRepository.save(config))
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSuccess(savedConfig -> cache.put(apiKey, newBucket(savedConfig)))
        .then();
  }

  public Mono<Void> deleteRateLimit(String apiKey) {
    return Mono.fromRunnable(() -> rateLimitConfigRepository.deleteById(apiKey))
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSuccess(v -> cache.remove(apiKey))
        .then();
  }

  public Mono<Void> reloadRateLimit(String apiKey) {
    return Mono.fromCallable(() -> rateLimitConfigRepository.findById(apiKey))
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap(optionalConfig -> optionalConfig.map(Mono::just).orElseGet(Mono::empty))
      .map(this::newBucket)
      .doOnNext(pair -> cache.put(apiKey, pair))
      .then();
  }


  public Page<Pair<RateLimitConfigDTO, Bucket>> findByCriteria(String apiKey, Pageable pageable) {
    return rateLimitConfigRepository.search(apiKey, pageable)
        .map(config -> {
            Pair<RateLimitConfigDTO, Bucket> pair = newBucket(config);
            cache.put(config.getApiKey(), pair);
            return pair;
        });
  }
}
