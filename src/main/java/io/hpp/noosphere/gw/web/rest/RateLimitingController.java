package io.hpp.noosphere.gw.web.rest;

import io.github.bucket4j.Bucket;
import io.hpp.noosphere.common.security.AuthoritiesConstants;
import io.hpp.noosphere.gw.service.RateLimitingService;
import io.hpp.noosphere.gw.service.dto.RateLimitConfigDTO;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/rate-limiting")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class RateLimitingController {

  private final RateLimitingService rateLimitingService;

  public RateLimitingController(RateLimitingService rateLimitingService) {
    this.rateLimitingService = rateLimitingService;
  }

  @GetMapping("/buckets")
  public Mono<Map<String, Bucket>> getBuckets() {
    return Mono.just(rateLimitingService.getCache());
  }

  @PostMapping("/configs/{key}")
  public Mono<ResponseEntity<Void>> updateConfig(@PathVariable String key, @RequestBody RateLimitConfigDTO config) {
    return rateLimitingService.updateRateLimit(key, config).then(Mono.just(ResponseEntity.ok().build()));
  }

  @PostMapping("/reload/{key}")
  public Mono<ResponseEntity<Void>> reloadRateLimit(@PathVariable String key) {
    return rateLimitingService.reloadRateLimit(key).then(Mono.just(ResponseEntity.ok().build()));
  }
}
