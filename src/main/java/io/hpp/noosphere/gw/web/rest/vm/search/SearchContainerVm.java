package io.hpp.noosphere.gw.web.rest.vm.search;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import lombok.Data;

@Data
public class SearchContainerVm {

    private String name;
    private String searchText;
    private Double price;
    private StatusCode statusCode;
    private String createdByUserId;
}
