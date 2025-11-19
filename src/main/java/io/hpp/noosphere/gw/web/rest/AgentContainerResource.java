package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.AgentContainerDTO;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentContainerVm;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agents")
public class AgentContainerResource {

    private final NoosphereHubClient noosphereHubClient;

    public AgentContainerResource(NoosphereHubClient noosphereHubClient) {
        this.noosphereHubClient = noosphereHubClient;
    }

    @PutMapping("/{agentId}/containers/{containerId}")
    public Mono<AgentContainerDTO> createAgentContainer(
        @PathVariable("agentId") UUID agentId,
        @PathVariable("containerId") UUID containerId
    ) {
        return noosphereHubClient.createAgentContainer(agentId, containerId);
    }

    @PostMapping("/{agentId}/containers/search")
    public Flux<AgentContainerDTO> searchAgentContainers(
        @PathVariable("agentId") UUID agentId,
        @RequestBody SearchAgentContainerVm searchVm,
        PageableVm pageable
    ) {
        return noosphereHubClient.searchAgentContainers(agentId, searchVm, pageable);
    }

    @GetMapping("/{agentId}/containers/{containerId}")
    public Mono<AgentContainerDTO> getAgentContainer(
        @PathVariable("agentId") UUID agentId,
        @PathVariable("containerId") UUID containerId
    ) {
        return noosphereHubClient.getAgentContainer(agentId, containerId);
    }

    @DeleteMapping("/{agentId}/containers/{containerId}")
    public Mono<Void> deleteAgentContainer(@PathVariable("agentId") UUID agentId, @PathVariable("containerId") UUID containerId) {
        return noosphereHubClient.deleteAgentContainer(agentId, containerId);
    }
}
