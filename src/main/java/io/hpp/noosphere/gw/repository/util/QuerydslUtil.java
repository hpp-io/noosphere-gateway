package io.hpp.noosphere.gw.repository.util;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public final class QuerydslUtil {

    private QuerydslUtil() {
    }

    public static <T> Page<T> fetchPage(JPQLQuery<T> query, Pageable pageable) {
        long total = query.fetchCount();
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        List<T> content = query.fetch();
        return new PageImpl<>(content, pageable, total);
    }
}
