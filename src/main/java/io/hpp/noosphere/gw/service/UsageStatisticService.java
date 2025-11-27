package io.hpp.noosphere.gw.service;

import io.hpp.noosphere.gw.domain.UsageStatistic;
import io.hpp.noosphere.gw.repository.UsageStatisticRepository;
import io.hpp.noosphere.gw.service.mapper.UsageStatisticMapper;
import io.hpp.noosphere.gw.web.rest.dto.UsageStatisticDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@Transactional
public class UsageStatisticService {

    private final UsageStatisticRepository usageStatisticRepository;
    private final UsageStatisticMapper usageStatisticMapper;

    public UsageStatisticService(
        UsageStatisticRepository usageStatisticRepository,
        UsageStatisticMapper usageStatisticMapper
    ) {
        this.usageStatisticRepository = usageStatisticRepository;
        this.usageStatisticMapper = usageStatisticMapper;
    }

    public Mono<UsageStatisticDTO> save(UsageStatisticDTO statisticDTO) {
        statisticDTO.setTimestamp(Instant.now());
        UsageStatistic statistic = usageStatisticMapper.toEntity(statisticDTO);
        statistic = usageStatisticRepository.save(statistic);
        return Mono.just(usageStatisticMapper.toDto(statistic));
    }

    @Transactional(readOnly = true)
    public Flux<UsageStatisticDTO> findByCriteria(String userId, String apiKey, String apiGroup, Instant startDate, Instant endDate, Pageable pageable) {
        return Flux.fromIterable(usageStatisticRepository.search(userId, apiKey, apiGroup, startDate, endDate, pageable))
            .map(usageStatisticMapper::toDto);
    }
}
