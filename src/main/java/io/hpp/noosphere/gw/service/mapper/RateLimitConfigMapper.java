package io.hpp.noosphere.gw.service.mapper;

import io.hpp.noosphere.gw.domain.RateLimitConfig;
import io.hpp.noosphere.gw.service.dto.RateLimitConfigDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RateLimitConfigMapper extends EntityMapper<RateLimitConfigDTO, RateLimitConfig> {
}
