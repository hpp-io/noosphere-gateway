package io.hpp.noosphere.gw.web.filter;

import static io.hpp.noosphere.gw.config.Constants.*;

import io.github.bucket4j.ConsumptionProbe;
import io.hpp.noosphere.common.service.util.CommonUtils;
import io.hpp.noosphere.gw.security.SecurityUtils;
import io.hpp.noosphere.gw.service.RateLimitService;
import io.hpp.noosphere.gw.service.UsageStatisticsService;
import io.hpp.noosphere.gw.service.dto.UsageStatisticsDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class RateLimitFilter implements WebFilter {

  private final RateLimitService rateLimitService;
  private final UsageStatisticsService usageStatisticsService;

  public RateLimitFilter(RateLimitService rateLimitService, UsageStatisticsService usageStatisticsService) {
    this.rateLimitService = rateLimitService;
    this.usageStatisticsService = usageStatisticsService;
  }

  @Override
  @NonNull
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    Instant startTime = Instant.now();
    String apiKey = exchange.getRequest().getHeaders().getFirst(HTTP_HEADER_API_KEY);
    String apiGroup = exchange.getRequest().getHeaders().getFirst(HTTP_HEADER_API_GROUP);
    String endpoint = exchange.getRequest().getPath().value();
    String method = exchange.getRequest().getMethod().name();

    Mono<String> userIdMono = Mono.defer(() -> {
      String userIdFromHeader = exchange.getRequest().getHeaders().getFirst(HTTP_HEADER_USER_ID);
      if (CommonUtils.isValid(userIdFromHeader)) {
        return Mono.just(userIdFromHeader);
      } else {
        String userId = SecurityUtils.getCurrentUserLoginWithSecurityContext().orElse(null);
        if (CommonUtils.isValid(userId)) {
          return Mono.just(userId);
        }
      }
      return SecurityUtils.getCurrentUserLogin();
    }).defaultIfEmpty("anonymous");

    return userIdMono.flatMap(userId -> {
      Mono<Void> chainMono;

      if (apiKey == null || apiKey.isEmpty()) {
        chainMono = chain.filter(exchange);
      } else {
        chainMono = rateLimitService
          .resolveBucket(apiKey)
          .flatMap(pair -> {
            ConsumptionProbe probe = pair.getRight().tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
              exchange.getResponse().getHeaders().add(HTTP_HEADER_RATE_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
              return chain.filter(exchange);
            } else {
              long waitForRefill = TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill());
              exchange.getResponse().getHeaders().add(HTTP_HEADER_RATE_LIMIT_RETRY_AFTER_MILLISECONDS, String.valueOf(waitForRefill));
              exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
              return exchange.getResponse().setComplete();
            }
          });
      }

      return chainMono.doFinally(signalType -> {
        Instant endTime = Instant.now();
        long duration = Duration.between(startTime, endTime).toMillis();
        Integer status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : null;

        UsageStatisticsDTO statisticDTO = new UsageStatisticsDTO(
          startTime,
          userId,
          apiKey,
          apiGroup,
          endpoint,
          method,
          status,
          duration
        );
        // TODO: This should be handled in a non-blocking way
        usageStatisticsService.save(statisticDTO).subscribe();
      });
    });
  }

}
