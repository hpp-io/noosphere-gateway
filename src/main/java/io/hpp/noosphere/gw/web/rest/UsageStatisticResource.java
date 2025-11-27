package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.service.UsageStatisticService;
import io.hpp.noosphere.gw.web.rest.dto.UsageStatisticDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/usage-statistics")
public class UsageStatisticResource {

    private final UsageStatisticService usageStatisticService;

    public UsageStatisticResource(UsageStatisticService usageStatisticService) {
        this.usageStatisticService = usageStatisticService;
    }

    @PostMapping
    public Mono<ResponseEntity<UsageStatisticDTO>> createUsageStatistic(@RequestBody UsageStatisticDTO usageStatisticDTO) {
        return usageStatisticService.save(usageStatisticDTO)
            .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<UsageStatisticDTO> getAllUsageStatistics(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String apiKey,
        @RequestParam(required = false) String apiGroup,
        @RequestParam(required = false) Optional<Instant> startDate,
        @RequestParam(required = false) Optional<Instant> endDate,
        Pageable pageable
    ) {
        return usageStatisticService.findByCriteria(
            userId,
            apiKey,
            apiGroup,
            startDate.orElse(null),
            endDate.orElse(null),
            pageable
        );
    }
}
