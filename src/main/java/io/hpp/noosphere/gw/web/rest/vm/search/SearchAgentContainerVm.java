package io.hpp.noosphere.gw.web.rest.vm.search;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import lombok.Data;

@Data
public class SearchAgentContainerVm {

    private String containerName;

    private StatusCode statusCode;
}
