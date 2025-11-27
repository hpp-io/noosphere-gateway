package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.service.UsageStatisticsService;
import io.hpp.noosphere.gw.service.dto.UsageStatisticsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

@RestController
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

    @GetMapping
    public Flux<UsageStatisticsDTO> getAllUsageStatistics(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String apiKey,
        @RequestParam(required = false) String apiGroup,
        @RequestParam(required = false) Optional<Instant> startDate,
        @RequestParam(required = false) Optional<Instant> endDate,
        Pageable pageable
    ) {
        return usageStatisticsService.findByCriteria(
            userId,
            apiKey,
            apiGroup,
            startDate.orElse(null),
            endDate.orElse(null),
            pageable
        );
    }
}
