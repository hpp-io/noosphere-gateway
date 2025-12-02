package io.hpp.noosphere.gw.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.common.repository.QuerydslUtil;
import io.hpp.noosphere.gw.domain.QRateLimitConfig;
import io.hpp.noosphere.gw.domain.RateLimitConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RateLimitConfigRepository extends JpaRepository<RateLimitConfig, String>, RateLimitConfigRepositoryCustom {

}

interface RateLimitConfigRepositoryCustom {

  Page<RateLimitConfig> search(String apiKey, Pageable pageable);
}

@Repository
class RateLimitConfigRepositoryCustomImpl implements RateLimitConfigRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public RateLimitConfigRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public Page<RateLimitConfig> search(String apiKey, Pageable pageable) {
    QRateLimitConfig qRateLimitConfig = QRateLimitConfig.rateLimitConfig;
    BooleanBuilder builder = new BooleanBuilder();

    if (apiKey != null) {
      builder.and(qRateLimitConfig.apiKey.containsIgnoreCase(apiKey));
    }

    JPQLQuery<RateLimitConfig> query = jpaQueryFactory.selectFrom(qRateLimitConfig).where(builder);
    return QuerydslUtil.fetchPage(query, pageable);
  }
}
