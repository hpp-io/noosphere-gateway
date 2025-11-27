package io.hpp.noosphere.gw.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.gw.domain.QUsageStatistics;
import io.hpp.noosphere.gw.domain.UsageStatistics;
import io.hpp.noosphere.gw.repository.util.QuerydslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

public interface UsageStatisticsRepository extends JpaRepository<UsageStatistics, Long>, UsageStatisticsRepositoryCustom {
}

interface UsageStatisticsRepositoryCustom {
    Page<UsageStatistics> search(String userId, String apiKey, String apiGroup, Instant startDate, Instant endDate, Pageable pageable);
}

@Repository
class UsageStatisticsRepositoryCustomImpl implements UsageStatisticsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public UsageStatisticsRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<UsageStatistics> search(String userId, String apiKey, String apiGroup, Instant startDate, Instant endDate, Pageable pageable) {
        QUsageStatistics qUsageStatistics = QUsageStatistics.usageStatistics;
        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(qUsageStatistics.userId.eq(userId));
        }
        if (apiKey != null) {
            builder.and(qUsageStatistics.apiKey.eq(apiKey));
        }
        if (apiGroup != null) {
            builder.and(qUsageStatistics.apiGroup.eq(apiGroup));
        }
        if (startDate != null) {
            builder.and(qUsageStatistics.timestamp.goe(startDate));
        }
        if (endDate != null) {
            builder.and(qUsageStatistics.timestamp.loe(endDate));
        }

        JPQLQuery<UsageStatistics> query = jpaQueryFactory.selectFrom(qUsageStatistics).where(builder);
        return QuerydslUtil.fetchPage(query, pageable);
    }
}
