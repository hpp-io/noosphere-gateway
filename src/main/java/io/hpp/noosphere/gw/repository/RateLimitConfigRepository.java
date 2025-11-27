package io.hpp.noosphere.gw.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.gw.domain.RateLimitConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RateLimitConfigRepository extends JpaRepository<RateLimitConfig, String>, RateLimitConfigRepositoryCustom {
}

interface RateLimitConfigRepositoryCustom {
    // Add custom methods here if needed in the future
}

@Repository
class RateLimitConfigRepositoryCustomImpl implements RateLimitConfigRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public RateLimitConfigRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
}
