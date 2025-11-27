package io.hpp.noosphere.gw.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.gw.domain.QUsageStatistic;
import io.hpp.noosphere.gw.domain.UsageStatistic;
import io.hpp.noosphere.gw.repository.util.QuerydslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

public interface UsageStatisticRepository extends JpaRepository<UsageStatistic, Long>, UsageStatisticRepositoryCustom {
}

interface UsageStatisticRepositoryCustom {
    Page<UsageStatistic> search(String userId, String apiKey, String apiGroup, Instant startDate, Instant endDate, Pageable pageable);
}

@Repository
class UsageStatisticRepositoryCustomImpl implements UsageStatisticRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public UsageStatisticRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<UsageStatistic> search(String userId, String apiKey, String apiGroup, Instant startDate, Instant endDate, Pageable pageable) {
        QUsageStatistic qUsageStatistic = QUsageStatistic.usageStatistic;
        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(qUsageStatistic.userId.eq(userId));
        }
        if (apiKey != null) {
            builder.and(qUsageStatistic.apiKey.eq(apiKey));
        }
        if (apiGroup != null) {
            builder.and(qUsageStatistic.apiGroup.eq(apiGroup));
        }
        if (startDate != null) {
            builder.and(qUsageStatistic.timestamp.goe(startDate));
        }
        if (endDate != null) {
            builder.and(qUsageStatistic.timestamp.loe(endDate));
        }

        JPQLQuery<UsageStatistic> query = jpaQueryFactory.selectFrom(qUsageStatistic).where(builder);
        return QuerydslUtil.fetchPage(query, pageable);
    }
}
