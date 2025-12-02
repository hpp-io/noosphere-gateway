package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.common.security.AuthoritiesConstants;
import io.hpp.noosphere.gw.service.RateLimitService;
import io.hpp.noosphere.gw.service.dto.RateLimitConfigDTO;
import io.hpp.noosphere.gw.web.rest.dto.BucketStateDTO;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.apache.commons.lang3.tuple.Pair;

@RestController
@RequestMapping("/api/admin/rate-limits")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class RateLimitResource {

  private final RateLimitService rateLimitService;

  public RateLimitResource(RateLimitService rateLimitService) {
    this.rateLimitService = rateLimitService;
  }

  @PostMapping("/{key}/reload")
  public Mono<ResponseEntity<Void>> reloadRateLimit(@PathVariable String key) {
    return rateLimitService.reloadRateLimit(key).then(Mono.just(ResponseEntity.ok().build()));
  }

  @PostMapping("")
  public Mono<ResponseEntity<RateLimitConfigDTO>> createRateLimit(
    @RequestBody RateLimitConfigDTO rateLimitConfigDTO
  ) {
    return rateLimitService
      .saveRateLimit(rateLimitConfigDTO.getApiKey(), rateLimitConfigDTO)
      .map(resultDTO -> ResponseEntity.ok().body(resultDTO));
  }

  @GetMapping("/{key}")
  public Mono<ResponseEntity<BucketStateDTO>> getRateLimit(@PathVariable String key) {
    return rateLimitService
      .resolveBucket(key)
      .filter(Objects::nonNull)
      .map(onePair -> new BucketStateDTO(key, onePair.getLeft(), onePair.getRight()))
      .map(bucketState -> ResponseEntity.ok().body(bucketState))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @PutMapping("/{key}")
  public Mono<ResponseEntity<RateLimitConfigDTO>> updateRateLimit(
    @PathVariable String key,
    @RequestBody RateLimitConfigDTO configDTO
  ) {
    return rateLimitService
      .saveRateLimit(key, configDTO)
      .map(savedConfig -> ResponseEntity.ok().body(savedConfig));
  }

  @DeleteMapping("/{key}")
  public Mono<ResponseEntity<Void>> deleteRateLimit(@PathVariable String key) {
    return rateLimitService.deleteRateLimit(key).then(Mono.just(ResponseEntity.noContent().build()));
  }

  @PostMapping("/search")
  public Mono<ResponseEntity<Flux<BucketStateDTO>>> searchRateLimits(
    @RequestBody Map<String, String> searchCriteria,
    Pageable pageable
  ) {
    return Mono
      .fromCallable(() ->
        rateLimitService.findByCriteria(searchCriteria.get("apiKey"), pageable)
      )
      .subscribeOn(Schedulers.boundedElastic())
      .map(page -> {
        Flux<BucketStateDTO> flux = Flux.fromIterable(page.getContent())
          .map(pair ->
            new BucketStateDTO(
              pair.getLeft().getApiKey(),
              pair.getLeft(),
              pair.getRight()
            )
          );
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(flux);
      });
  }
}
