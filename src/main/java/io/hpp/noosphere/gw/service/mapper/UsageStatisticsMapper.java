package io.hpp.noosphere.gw.service.mapper;

import io.hpp.noosphere.gw.domain.UsageStatistics;
import io.hpp.noosphere.gw.service.dto.UsageStatisticsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsageStatisticsMapper extends EntityMapper<UsageStatisticsDTO, UsageStatistics> {
}
