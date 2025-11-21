package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.config.RateLimited;
import io.hpp.noosphere.gw.web.rest.dto.AgentDTO;
import io.hpp.noosphere.gw.web.rest.dto.UserSubscriptionDTO;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.RegisterAgentVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentVm;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agents")
public class AgentResource {

  private final NoosphereHubClient noosphereHubClient;

  public AgentResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  @PutMapping("/{id}")
  @RateLimited
  public Mono<AgentDTO> updateAgent(@PathVariable("id") UUID id, @RequestBody AgentDTO agentDTO) {
    return noosphereHubClient.updateAgent(id, agentDTO);
  }

  @PostMapping("/search")
  @RateLimited
  public Flux<AgentDTO> searchAgents(@RequestBody SearchAgentVm searchVm, PageableVm pageable) {
    return noosphereHubClient.searchAgents(searchVm, pageable);
  }

  @GetMapping("/{id}")
  @RateLimited
  public Mono<AgentDTO> getAgent(@PathVariable("id") UUID id) {
    return noosphereHubClient.getAgent(id);
  }

  @DeleteMapping("/{id}")
  @RateLimited
  public Mono<Void> deleteAgent(@PathVariable("id") UUID id) {
    return noosphereHubClient.deleteAgent(id);
  }

  @PostMapping("/register")
  public Mono<AgentDTO> registerAgent(@RequestBody RegisterAgentVm agentVm) {
    return noosphereHubClient.registerAgent(agentVm);
  }

  @GetMapping("/{id}/subscriptions")
  @RateLimited
  public Flux<UserSubscriptionDTO> getSubscriptions(
    @PathVariable("id") UUID id,
    @RequestParam(value = "size", required = true) final Integer size
  ) {
    return noosphereHubClient.getSubscriptions(id, size);
  }
}
