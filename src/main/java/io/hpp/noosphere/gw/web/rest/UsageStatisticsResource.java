package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.common.security.AuthoritiesConstants;
import io.hpp.noosphere.gw.service.UsageStatisticsService;
import io.hpp.noosphere.gw.service.dto.UsageStatisticsDTO;
import java.time.Instant;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
@RequestMapping("/api/usage-statistics")
public class UsageStatisticsResource {

  private final UsageStatisticsService usageStatisticsService;

  public UsageStatisticsResource(UsageStatisticsService usageStatisticsService) {
    this.usageStatisticsService = usageStatisticsService;
  }

  @PostMapping
  public Mono<ResponseEntity<UsageStatisticsDTO>> createUsageStatistic(@RequestBody UsageStatisticsDTO usageStatisticsDTO) {
    return usageStatisticsService.save(usageStatisticsDTO)
      .map(ResponseEntity::ok);
  }

  @PostMapping("/search")
  public Mono<ResponseEntity<Flux<UsageStatisticsDTO>>> searchUsageStatistics(
    @RequestBody Map<String, String> searchCriteria,
    Pageable pageable
  ) {
    return Mono
      .fromCallable(() ->
        usageStatisticsService.findByCriteria(
          searchCriteria.get("userId"),
          searchCriteria.get("apiKey"),
          searchCriteria.get("apiGroup"),
          searchCriteria.containsKey("startDate") ? Instant.parse(searchCriteria.get("startDate")) : null,
          searchCriteria.containsKey("endDate") ? Instant.parse(searchCriteria.get("endDate")) : null,
          pageable)
      )
      .subscribeOn(Schedulers.boundedElastic())
      .map(page -> {
        Flux<UsageStatisticsDTO> flux = Flux.fromIterable(page.getContent());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(flux);
      });
  }
}
