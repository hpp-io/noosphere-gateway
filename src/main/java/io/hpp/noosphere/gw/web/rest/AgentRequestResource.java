package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.AgentRequestDTO;
import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentRequestVm;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agent-requests")
public class AgentRequestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AgentRequestResource.class);

  private static final String ENTITY_NAME = "agentRequest";

  private final NoosphereHubClient noosphereHubClient;

  public AgentRequestResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  @PostMapping
  public Mono<AgentRequestDTO> createAgentRequest(@Valid @RequestBody AgentRequestDTO agentRequestDTO) {
    LOG.debug("REST request to create AgentRequest : {}", agentRequestDTO);
    if (agentRequestDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new agentRequest cannot already have an ID", ENTITY_NAME, "idexists"));
    }
    return noosphereHubClient.createAgentRequest(agentRequestDTO);
  }

  @PostMapping("/search")
  public Flux<AgentRequestDTO> searchAgentRequests(
    @Valid @RequestBody SearchAgentRequestVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search AgentRequests");
    return noosphereHubClient.searchAgentRequests(searchCriteria, new PageableVm(pageable));
  }

  @GetMapping("/{id}")
  public Mono<AgentRequestDTO> getAgentRequest(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get AgentRequest : {}", id);
    return noosphereHubClient.getAgentRequest(id);
  }
}