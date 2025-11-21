package io.hpp.noosphere.gw.web.rest;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.hpp.noosphere.gw.service.RateLimitingService;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class RateLimitingFilter implements WebFilter {

  private final RateLimitingService rateLimitingService;

  public RateLimitingFilter(RateLimitingService rateLimitingService) {
    this.rateLimitingService = rateLimitingService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String apiKey = exchange.getRequest().getHeaders().getFirst("X-api-key");
    if (apiKey == null || apiKey.isEmpty()) {
      return chain.filter(exchange);
    }

    return rateLimitingService
      .resolveBucket(apiKey)
      .flatMap(bucket -> {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
          exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
          return chain.filter(exchange);
        } else {
          long waitForRefill = TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill());
          exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After-Milliseconds", String.valueOf(waitForRefill));
          exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
          return exchange.getResponse().setComplete();
        }
      });
  }
}
