package io.hpp.noosphere.gw.web.rest.vm;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

/**
 * A ViewModel for representing Spring's Pageable data in a format
 * that is safe for Feign's QueryMap encoder. This avoids issues
 * with serialVersionUID in the standard Pageable implementation.
 */
public class PageableVm {

    private Integer page;
    private Integer size;
    private List<String> sort;

    public PageableVm() {
        // Empty constructor needed for Jackson deserialization
    }

    public PageableVm(Pageable pageable) {
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        if (pageable.getSort().isSorted()) {
            this.sort = pageable.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .collect(Collectors.toList());
        }
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public List<String> getSort() {
        return sort;
    }
}