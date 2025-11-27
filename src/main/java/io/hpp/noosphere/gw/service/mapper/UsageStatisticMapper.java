package io.hpp.noosphere.gw.service.mapper;

import io.hpp.noosphere.gw.domain.UsageStatistic;
import io.hpp.noosphere.gw.web.rest.dto.UsageStatisticDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsageStatisticMapper extends EntityMapper<UsageStatisticDTO, UsageStatistic> {
}
