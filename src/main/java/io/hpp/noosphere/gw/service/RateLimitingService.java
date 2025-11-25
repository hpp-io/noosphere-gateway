package io.hpp.noosphere.gw.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.service.dto.RateLimitConfig;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RateLimitingService {

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
  private final RateLimitConfig defaultConfig = new RateLimitConfig(null, 10, 10, 60, 0);

  private final NoosphereHubClient noosphereHubClient;

  public RateLimitingService(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  public Mono<Bucket> resolveBucket(String key) {
    if (cache.containsKey(key)) {
      return Mono.just(cache.get(key));
    }
    return noosphereHubClient
      .getRateLimitConfig(key)
      .map(this::newBucket)
      .doOnNext(bucket -> cache.put(key, bucket))
      .switchIfEmpty(Mono.fromCallable(() -> {
        Bucket defaultBucket = newBucket(defaultConfig);
        cache.put(key, defaultBucket);
        return defaultBucket;
      }))
      .onErrorResume(throwable -> {
        Bucket defaultBucket = newBucket(defaultConfig);
        cache.put(key, defaultBucket);
        return Mono.just(defaultBucket);
      });
  }

  private Bucket newBucket(RateLimitConfig config) {
    return new LocalBucketBuilder()
      .addLimit(
        Bandwidth.classic(
          config.getCapacity(),
          Refill.greedy(config.getRefillAmount(), Duration.ofSeconds(config.getRefillTimeInSeconds()))
        )
      )
      .build();
  }

  public Mono<Void> updateRateLimit(String key, RateLimitConfig config) {
    return noosphereHubClient
      .updateRateLimitConfig(key, config)
      .doOnSuccess(v -> {
        cache.put(key, newBucket(config));
      });
  }

  public Mono<Void> reloadRateLimit(String key) {
    return noosphereHubClient
      .getRateLimitConfig(key)
      .map(this::newBucket)
      .doOnNext(bucket -> cache.put(key, bucket))
      .then();
  }

  public Map<String, Bucket> getCache() {
    return cache;
  }

  public Flux<RateLimitConfig> getConfigCache(Pageable pageable) {
    return noosphereHubClient.getRateLimitConfigs(new PageableVm(pageable));
  }

  public Mono<Void> cacheAllRateLimitConfigs() {
    return fetchPage(0)
      .expand(configs -> {
        if (configs.isEmpty()) {
          return Mono.empty();
        }
        return fetchPage(configs.get(configs.size() - 1).getPage() + 1);
      })
      .flatMap(Flux::fromIterable)
      .doOnNext(config -> cache.put(config.getKey(), newBucket(config)))
      .then();
  }

  private Mono<java.util.List<RateLimitConfig>> fetchPage(int page) {
    return noosphereHubClient.getRateLimitConfigs(new PageableVm(PageRequest.of(page, 1000))).collectList();
  }
}
