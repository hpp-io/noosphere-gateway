package io.hpp.noosphere.gw.service;

import io.hpp.noosphere.gw.domain.UsageStatistics;
import io.hpp.noosphere.gw.repository.UsageStatisticsRepository;
import io.hpp.noosphere.gw.service.dto.UsageStatisticsDTO;
import io.hpp.noosphere.gw.service.mapper.UsageStatisticsMapper;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class UsageStatisticsService {

  private final UsageStatisticsRepository usageStatisticsRepository;
  private final UsageStatisticsMapper usageStatisticsMapper;

  public UsageStatisticsService(
    UsageStatisticsRepository usageStatisticsRepository,
    UsageStatisticsMapper usageStatisticsMapper
  ) {
    this.usageStatisticsRepository = usageStatisticsRepository;
    this.usageStatisticsMapper = usageStatisticsMapper;
  }

  public Mono<UsageStatisticsDTO> save(UsageStatisticsDTO statisticDTO) {
    statisticDTO.setTimestamp(Instant.now());
    UsageStatistics statistic = usageStatisticsMapper.toEntity(statisticDTO);
    statistic = usageStatisticsRepository.save(statistic);
    return Mono.just(usageStatisticsMapper.toDto(statistic));
  }

  @Transactional(readOnly = true)
  public Page<UsageStatisticsDTO> findByCriteria(String userId, String apiKey, String apiGroup, Instant startDate, Instant endDate, Pageable pageable) {
    return usageStatisticsRepository.search(userId, apiKey, apiGroup, startDate, endDate, pageable)
      .map(usageStatisticsMapper::toDto);
  }
}
