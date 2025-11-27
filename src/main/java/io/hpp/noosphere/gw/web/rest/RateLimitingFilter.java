package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.common.config.Constants.HTTP_HEADER_API_KEY;

import io.github.bucket4j.ConsumptionProbe;
import io.hpp.noosphere.gw.service.RateLimitingService;
import io.hpp.noosphere.gw.service.UsageStatisticService;
import io.hpp.noosphere.gw.web.rest.dto.UsageStatisticDTO;
import java.time.Duration;
import java.time.Instant;
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
  private final UsageStatisticService usageStatisticService;

  public RateLimitingFilter(RateLimitingService rateLimitingService, UsageStatisticService usageStatisticService) {
    this.rateLimitingService = rateLimitingService;
    this.usageStatisticService = usageStatisticService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    Instant startTime = Instant.now();

    String apiKey = exchange.getRequest().getHeaders().getFirst(HTTP_HEADER_API_KEY);
    String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID"); // Assuming User ID can be passed in a header
    String apiGroup = exchange.getRequest().getHeaders().getFirst("X-API-Group"); // Assuming API Group can be passed in a header
    String endpoint = exchange.getRequest().getPath().value();
    String method = exchange.getRequest().getMethod().name();

    Mono<Void> chainMono;

    if (apiKey == null || apiKey.isEmpty()) {
      chainMono = chain.filter(exchange);
    } else {
      chainMono = rateLimitingService
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

    return chainMono.doFinally(signalType -> {
      Instant endTime = Instant.now();
      long duration = Duration.between(startTime, endTime).toMillis();
      Integer status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : null;

      UsageStatisticDTO statisticDTO = new UsageStatisticDTO(
        startTime,
        userId,
        apiKey,
        apiGroup,
        endpoint,
        method,
        status,
        duration
      );
      usageStatisticService.save(statisticDTO).subscribe(); // Save asynchronously
    });
  }
}
