package io.hpp.noosphere.gw.web.rest.vm.search;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.util.UUID;
import lombok.Data;

@Data
public class SearchAgentRequestVm {

    private String agentName;
    private UUID containerId;
    private UUID agentId;
    private StatusCode statusCode;
}
